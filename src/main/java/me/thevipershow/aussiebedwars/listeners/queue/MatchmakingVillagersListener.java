package me.thevipershow.aussiebedwars.listeners.queue;

import java.sql.Connection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameManager;
import me.thevipershow.aussiebedwars.storage.sql.MySQLDatabase;
import me.thevipershow.aussiebedwars.storage.sql.queue.QueueTableUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;

public class MatchmakingVillagersListener implements Listener {

    private final Plugin plugin;
    private final GameManager gameManager;
    // private final QueueLoader queueLoader;

    public MatchmakingVillagersListener(Plugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAtEntity(final PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager)) return;
        final UUID uuid = entity.getUniqueId();
        if (!player.getWorld().equals(gameManager.getWorldsManager().getLobbyWorld())) return;
        event.setCancelled(true);

        final Optional<Connection> conn = MySQLDatabase.getConnection();

        if (!conn.isPresent()) return;

        final CompletableFuture<Optional<Gamemode>> future = QueueTableUtils.getVillagerGamemode(uuid, conn.get());

        future.thenAccept((g) -> {

            if (!g.isPresent()) return;

            final Optional<ActiveGame> opt = gameManager.findOptimalGame(g.get());

            if (!opt.isPresent()) {
                player.sendMessage(AussieBedwars.PREFIX + "§eWe could not find a game.");
                return;
            }

            final ActiveGame activeGame = opt.get();

            gameManager.removeFromAllQueues(player);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> activeGame.moveToWaitingRoom(player), 1L);
        });

    }
}
