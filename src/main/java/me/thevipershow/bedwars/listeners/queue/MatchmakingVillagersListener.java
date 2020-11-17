package me.thevipershow.bedwars.listeners.queue;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.game.AbstractQueue;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameManager;
import me.thevipershow.bedwars.game.objects.TeamManager;
import me.thevipershow.bedwars.storage.sql.MySQLDatabase;
import me.thevipershow.bedwars.storage.sql.tables.QueueTableUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;

public final class MatchmakingVillagersListener implements Listener {

    private final Plugin plugin;
    private final GameManager gameManager;

    private final HashMap<Player, Long> lastClick = new HashMap<>();

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

        if (lastClick.containsKey(player)) {
            final long lastClick = this.lastClick.get(player);
            final long now = System.currentTimeMillis();
            if (now - lastClick < 1000) {
                return;
            } else {
                this.lastClick.put(player, now);
            }
        }

        lastClick.put(player, System.currentTimeMillis());

        final Optional<Connection> conn = MySQLDatabase.getConnection();

        if (!conn.isPresent()) {
            return;
        }

        final CompletableFuture<Optional<Gamemode>> future = QueueTableUtils.getVillagerGamemode(uuid, plugin);

        future.thenAccept((g) -> plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!g.isPresent()) return;

            final Optional<ActiveGame> opt = gameManager.findOptimalGame(g.get());

            if (!opt.isPresent()) {

                if (!gameManager.isLoading()) {
                    gameManager.loadRandom(g.get());
                    return;
                }

                player.sendMessage(Bedwars.PREFIX + AllStrings.WE_COULD_NOT_FIND_GAME.get());
                return;
            }

            final ActiveGame activeGame = opt.get();

            final ActiveGame playerCurrentGame = gameManager.getPlayerCurrentGame(player);
            playerCurrentGame.getGameLobbyTicker().getAssociatedQueue().removeFromQueue(player);
        //    gameManager.removeFromAllQueues(player);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> gameManager.addToQueue(player, activeGame), 1L);
        }, 1L));

    }
}
