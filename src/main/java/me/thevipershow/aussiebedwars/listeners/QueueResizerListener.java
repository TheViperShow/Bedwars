package me.thevipershow.aussiebedwars.listeners;

import me.thevipershow.aussiebedwars.game.GameManager;
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
    }
}
