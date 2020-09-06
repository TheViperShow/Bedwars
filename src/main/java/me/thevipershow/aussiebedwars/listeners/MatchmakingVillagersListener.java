package me.thevipershow.aussiebedwars.listeners;

import java.util.UUID;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameManager;
import me.thevipershow.aussiebedwars.game.QueueLoader;
import me.thevipershow.aussiebedwars.storage.sql.MySQLDatabase;
import me.thevipershow.aussiebedwars.storage.sql.queue.QueueTableUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.Plugin;

public class MatchmakingVillagersListener implements Listener {

    private final Plugin plugin;
    private final GameManager gameManager;
    // private final QueueLoader queueLoader;

    public MatchmakingVillagersListener(Plugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    private static void connectedToQueue(final Player player, final ActiveGame activeGame) {
        player.sendMessage(AussieBedwars.PREFIX + "§eYou have joined §7" + activeGame.getLobbyWorldName() + " §equeue");
        player.sendMessage(AussieBedwars.PREFIX + String.format("§eStatus §7[§a%d§8/§a%d§7]", activeGame.getAssociatedQueue().queueSize(), activeGame.getBedwarsGame().getPlayers()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager)) return;

        final UUID uuid = entity.getUniqueId();

        MySQLDatabase.getConnection()
                .ifPresent(connection -> QueueTableUtils.getVillagerGamemode(uuid, connection)
                        .thenAccept(gamemode -> gamemode.ifPresent(g -> gameManager.findOptimalGame(g)
                                .ifPresent(activeGame -> {
                                    gameManager.removeFromAllQueues(player);
                                    if (activeGame.getAssociatedQueue().addToQueue(player)) {
                                        connectedToQueue(player, activeGame);
                                        activeGame.moveToLobby(player);
                                    }
                                }))));
    }
}
