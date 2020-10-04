package me.thevipershow.aussiebedwars.listeners.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Map;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.SpawnPosition;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.UpgradeType;
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
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public final class PlayerDeathListener extends UnregisterableListener {

    private final ActiveGame activeGame;
    private static final ItemStack LOBBY_COMPASS = new ItemStack(Material.COMPASS, 0x01);

    public PlayerDeathListener(final ActiveGame activeGame) {
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

    public static class RespawnRunnable extends BukkitRunnable {

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
                final PacketPlayOutTitle emptyTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatMessage(""), 2, 16, 2);
                final IChatBaseComponent iChat = new ChatMessage(AussieBedwars.PREFIX + String.format("§eRespawning in §7%d §es", secondsLeft));
                final PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, iChat, 2, 16, 2);
                conn.sendPacket(emptyTitle);
                conn.sendPacket(titlePacket);
                secondsLeft--;
            }
        }
    }

    public static void givePlayerLobbyCompass(final Player p) {
        p.getInventory().setItemInHand(LOBBY_COMPASS);
    }

    public static void doDeathTimer(final Player p, final ActiveGame activeGame) {
        activeGame.getPlayersRespawning().add(p);
        new RespawnRunnable(p, activeGame).runTaskTimer(activeGame.getPlugin(), 1L, 20L);
        activeGame.getPlugin().getServer().getScheduler().runTaskLater(activeGame.getPlugin(), () -> activeGame.getPlayersRespawning().remove(p), 20L*5L);
    }

    public static void clearInvExceptArmorAndTools(final Player player, final ActiveGame game) {
        final PlayerInventory inv = player.getInventory();
        final ItemStack[] contents = inv.getContents();

        for (int i = 0; i < contents.length; i++) {
            final ItemStack stack = contents[i];
            if (stack == null) {
                continue;
            } else if (stack.getType().name().endsWith("_SWORD")) { // Checking if player has sword
                if (stack.getType() != Material.WOOD_SWORD) {
                    final Map<Enchantment, Integer> availableEnchs = stack.getEnchantments();
                    if (availableEnchs.isEmpty()) {
                        inv.setItem(i, new ItemStack(Material.WOOD_SWORD, 1));
                    } else {
                        final ItemStack es = new ItemStack(Material.WOOD_SWORD, 1);
                        es.addEnchantments(availableEnchs);
                        inv.setItem(i, es);
                    }
                }
                continue;
            } else if (game.getBedwarsGame().getShop().getUpgradeItems()
                    .stream()
                    .flatMap(shop -> shop.getLevels().stream())
                    .anyMatch(lvl -> lvl.getCachedGameStack().isSimilar(stack))) {
                continue;
            }
            inv.setItem(i, null);
        }

        game.downgradePlayerTools(player);

        final int enchantLvl = game.getUpgradesLevelsMap().get(UpgradeType.SHARPNESS).get(game.getPlayerTeam(player));
        if (enchantLvl != 0) {
            GameUtils.enchantSwords(Enchantment.DAMAGE_ALL, enchantLvl, player); // Adding enchant if he has the Upgrade.
        }
    }

    public static String generateDeathMessage(final EntityDamageEvent e, final BedwarsTeam killedPlayerTeam, final boolean finalKill, ActiveGame activeGame) {

        final StringBuilder msg = new StringBuilder("§" + killedPlayerTeam.getColorCode() + e.getEntity().getName());
        if (e instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
            final Entity damager = ee.getDamager();
            if (damager instanceof Player) {
                final Player p = (Player) damager;
                final BedwarsTeam damagerTeam = activeGame.getPlayerTeam(p);
                final DamageCause cz = ee.getCause();
                final String killMsg = cz == DamageCause.PROJECTILE ? "pierced " : "annihilated ";
                msg.append(String.format(" §7was %s by §", killMsg)).append(damagerTeam.getColorCode()).append(p.getName()).append('.');
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
                    case ENTITY_EXPLOSION:
                        msg.append(" §7exploded into pieces.");
                        break;
                    default:
                        msg.append(" §7has died.");
                        break;
                }
            }
        }
        if (finalKill) {
            msg.append(" §e§lFINAL KILL.");
        }
        return msg.toString();
    }

    public static void deathLogic(ActiveGame activeGame, BedwarsTeam b, Player p, EntityDamageEvent event) {
        if (activeGame.getDestroyedTeams().contains(b) || activeGame.getAbstractDeathmatch().isRunning()) { // Checking if players' team's bed has been broken previously.
            // here player has lost the game.
            // or has died permanently since the deathmatch mode is ON.
            activeGame.removePlayer(p);
            p.setAllowFlight(true);
            p.setFlying(true);
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 42069, 1, false), true);
            p.getInventory().clear();
            GameUtils.clearArmor(p);
            givePlayerLobbyCompass(p);
            if (!activeGame.isOutOfGame(p)) {
                p.sendMessage(AussieBedwars.PREFIX + "§cYou have been eliminated.");
                final String generatedDeathMsg = generateDeathMessage(event, b, true, activeGame);
                activeGame.getAssociatedWorld().getPlayers().forEach(player -> player.sendMessage(AussieBedwars.PREFIX + generatedDeathMsg));
            }
            activeGame.getPlayersOutOfGame().add(p);

            final Location pLoc = p.getLocation();
            if (pLoc.getY() <= -5.00) {
                pLoc.setY(90.00);
                p.teleport(pLoc);
            }

            if (!activeGame.isWinnerDeclared()) {
                final BedwarsTeam bedwarsTeam = activeGame.findWinningTeam();
                if (bedwarsTeam != null) {
                    activeGame.declareWinner(bedwarsTeam);
                    activeGame.getPlugin().getServer().getScheduler().runTaskLater(activeGame.getPlugin(), activeGame::stop, 20 * 10L);
                }
            }
        } else { // team is still in game
            p.setGameMode(GameMode.SPECTATOR);
            clearInvExceptArmorAndTools(p, activeGame);
            doDeathTimer(p, activeGame);
            final Location pLoc = p.getLocation();
            if (pLoc.getY() <= -5.00) {
                pLoc.setY(90.00);
                p.teleport(pLoc);
            }

            final String generatedDeathMsg = generateDeathMessage(event, b, false, activeGame);
            activeGame.getAssociatedWorld().getPlayers().forEach(player -> player.sendMessage(AussieBedwars.PREFIX + generatedDeathMsg));
        }

        p.setHealth(p.getMaxHealth());
    }

    @EventHandler()
    public final void onPlayerDeath(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        final Player p = (Player) event.getEntity();
        final World w = p.getWorld();

        if (!w.equals(activeGame.getAssociatedWorld())) {
            return;
        }

        final double playerHealth = p.getHealth();
        final double damage = event.getDamage();

        if (playerHealth - damage <= 0.00) {
            final BedwarsTeam b = activeGame.getPlayerTeam(p);

            p.closeInventory();
            deathLogic(activeGame, b, p, event);
            event.setCancelled(true);

            if (event instanceof EntityDamageByEntityEvent) {
                final EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) event;
                final Entity damager = edbee.getDamager();
                if (damager instanceof Player) {
                    GameUtils.sendKillActionBar(activeGame, (Player) damager,  p);
                    ((Player) damager).playSound(damager.getLocation(), Sound.SPLASH, 8.50f, 0.85f);
                }
            }
        }
    }
}
