package me.thevipershow.bedwars.listeners.queue;

import me.thevipershow.bedwars.game.GameManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QueueResizerListener implements Listener {

    private final GameManager gameManager;

    public QueueResizerListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        gameManager.removeFromAllQueues(event.getPlayer());
        event.getPlayer().setGameMode(GameMode.SURVIVAL);
    }
}
