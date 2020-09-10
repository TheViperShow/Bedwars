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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
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
                "§7- You can simply click on it and you will be automatically teleported.",
                "§7- Remember that you can't join this game once you've left."
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
                if (spawnPos != null)
                    p.teleport(spawnPos.toLocation(activeGame.getAssociatedWorld()));
                cancel();
            } else {
                final PlayerConnection conn = GameUtils.getPlayerConnection(p);
                final IChatBaseComponent iChat = new ChatMessage(AussieBedwars.PREFIX + String.format("§erespawning in §7%d §eseconds", secondsLeft));
                final PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, iChat, 2, 16, 2);
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player p = event.getEntity();
        final World w = p.getWorld();
        if (!w.equals(activeGame.getAssociatedWorld())) return;

        final BedwarsTeam b = activeGame.getPlayerTeam(p);
        if (activeGame.getDestroyedTeams().contains(b)) { // Checking if players' team's bed has been broken previously.
            // here player has lost the game.
            p.setGameMode(GameMode.SPECTATOR);
            givePlayerLobbyCompass(p);
            activeGame.removePlayer(p);
        } else { // player is still in game
            doDeathTimer(p);
        }
    }
}
