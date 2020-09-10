package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerQuitDuringGameListener extends UnregisterableListener {
    private final ActiveGame activeGame;

    public PlayerQuitDuringGameListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player p = event.getPlayer();
        final World w = p.getWorld();
        if (!w.equals(activeGame.getAssociatedWorld())) return;

        activeGame.removePlayer(p);
    }
}
