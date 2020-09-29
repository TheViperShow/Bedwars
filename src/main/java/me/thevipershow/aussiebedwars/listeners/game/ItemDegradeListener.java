package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemDamageEvent;

public final class ItemDegradeListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public ItemDegradeListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemConsume(final PlayerItemDamageEvent event) {
        final Player player = event.getPlayer();

        if (!player.getWorld().equals(activeGame.getAssociatedWorld())) {
            return;
        }

        event.setCancelled(true);
    }
}
