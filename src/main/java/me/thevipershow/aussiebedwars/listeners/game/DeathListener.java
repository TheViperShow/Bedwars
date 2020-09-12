package me.thevipershow.aussiebedwars.listeners.game;

import com.google.common.collect.Lists;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.SpawnPosition;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameUtils;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public final class DeathListener extends UnregisterableListener {

    private final ActiveGame activeGame;
    private RespawnRunnable respawnRunnable = null;
    private static final ItemStack LOBBY_COMPASS = new ItemStack(Material.COMPASS, 1);

    public DeathListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;

        final ItemMeta compassMeta = LOBBY_COMPASS.getItemMeta();
        compassMeta.setDisplayName(AussieBedwars.PREFIX + "§c§lReturn to lobby");
        compassMeta.setLore(Lists.newArrayList(
                "§7- You can use this compass to return to the server's lobby",
                "§7  You can simply click on it and you will be automatically teleported.",
                "§7  Remember that you can't join this game once you've left."
        ));
        LOBBY_COMPASS.setItemMeta(compassMeta);
    }

    private static class RespawnRunnable extends BukkitRunnable {

        private int secondsLeft = 5;
        private final Player p;
        private final ActiveGame activeGame;

        public RespawnRunnable(final Player p, final ActiveGame activeGame) {
            this.p = p;
            this.activeGame = activeGame;
        }

        @Override
        public final void run() {
            if (!p.isOnline() && p.getWorld().equals(activeGame.getAssociatedWorld())) {
                cancel();
            } else if (secondsLeft == 0) {
                final SpawnPosition spawnPos = activeGame.getBedwarsGame().spawnPosOfTeam(activeGame.getPlayerTeam(p));
                if (spawnPos != null) {
                    p.teleport(spawnPos.toLocation(activeGame.getAssociatedWorld()));
                    p.setGameMode(GameMode.SURVIVAL);
                }
                cancel();
            } else {
                final PlayerConnection conn = GameUtils.getPlayerConnection(p);
                final PacketPlayOutTitle emptyTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatMessage(" "), 2, 16,2);
                final IChatBaseComponent iChat = new ChatMessage(AussieBedwars.PREFIX + String.format("§eRespawning in §7%d §es", secondsLeft));
                final PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, iChat, 2, 16, 2);
                conn.sendPacket(emptyTitle);
                conn.sendPacket(titlePacket);
                secondsLeft--;
            }
        }
    }

    private void givePlayerLobbyCompass(final Player p) {
        p.getInventory().clear();
        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), LOBBY_COMPASS);
    }

    private void doDeathTimer(final Player p) {
        new RespawnRunnable(p, activeGame).runTaskTimer(activeGame.getPlugin(), 1L, 20L);
    }

    private static void setFakeSpectator(final Player p) {

    }

    private static void clearEverythingExceptArmour(final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack[] armorContents = playerInventory.getArmorContents().clone();
        playerInventory.clear();
        playerInventory.setArmorContents(armorContents);
    }

    private String generateDeathMessage(final EntityDamageEvent e, final BedwarsTeam killedPlayerTeam) {
        final StringBuilder msg = new StringBuilder("§" + killedPlayerTeam.getColorCode() + e.getEntity().getName());
        if (e instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
            final Entity damager = ee.getDamager();
            if (damager instanceof Player) {
                final Player p = (Player) damager;
                final BedwarsTeam damagerTeam = activeGame.getPlayerTeam(p);
                msg.append(" §7was annihilated by §").append(damagerTeam.getColorCode()).append(p.getName()).append('.');
            } else {
                msg.append(" §7was killed by §f").append(GameUtils.beautifyCaps(damager.getType().name())).append('.');
            }
        } else {
            final DamageCause damageCause = e.getCause();
            switch (damageCause) {
                case FIRE:
                case LAVA:
                    msg.append(" §7burned to death.");
                    break;
                case DROWNING:
                    msg.append(" §7tried to swallow the ocean.");
                    break;
                case SUFFOCATION:
                    msg.append(" §7suffocated to death.");
                    break;
                case FALL:
                    msg.append(" §7believed the floor was soft.");
                    break;
                case VOID:
                    msg.append(" §7fought against gravity.");
                    break;
                case BLOCK_EXPLOSION:
                    msg.append(" §7exploded into pieces.");
                    break;
                default:
                    msg.append(" §7has died.");
                    break;
            }
        }
        msg.append(" §e§lFINAL KILL.");
        return msg.toString();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public final void onPlayerDeath(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final Player p = (Player) event.getEntity();
        final World w = p.getWorld();

        if (!w.equals(activeGame.getAssociatedWorld())) return;

        final double playerHealth = p.getHealth();
        final double damage = event.getDamage();
        if (playerHealth - damage <= 0.0) {
            final BedwarsTeam b = activeGame.getPlayerTeam(p);
            if (activeGame.getDestroyedTeams().contains(b)) { // Checking if players' team's bed has been broken previously.
                // here player has lost the game.
                givePlayerLobbyCompass(p);
                if (!activeGame.getPlayersOutOfGame().contains(p)) {
                    p.sendMessage("§cYou have been eliminated.");
                    final String generatedDeathMsg = generateDeathMessage(event, b);
                    activeGame.getAssociatedWorld().getPlayers().forEach(player -> player.sendMessage(generatedDeathMsg));
                }

                activeGame.getPlayersOutOfGame().add(p);
                activeGame.removePlayer(p);

                if (!activeGame.isWinnerDeclared()) {
                    final BedwarsTeam bedwarsTeam = activeGame.findWinningTeam();
                    if (bedwarsTeam != null) {
                        activeGame.declareWinner(bedwarsTeam);
                        activeGame.getPlugin().getServer().getScheduler().runTaskLater(activeGame.getPlugin(), activeGame::stop, 20 * 15L);
                    }
                }
            } else { // team is still in game
                p.setGameMode(GameMode.SPECTATOR);
                doDeathTimer(p);
                final Location pLoc = p.getLocation();
                if (pLoc.getY() <= 5.00) {
                    pLoc.setY(70.00);
                    p.teleport(pLoc);
                }
            }
            p.setHealth(p.getMaxHealth());
            clearEverythingExceptArmour(p); //TODO: ->
            event.setCancelled(true);
        }
    }
}