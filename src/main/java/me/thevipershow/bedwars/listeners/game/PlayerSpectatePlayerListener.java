package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public final class PlayerSpectatePlayerListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public PlayerSpectatePlayerListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        if (!player.getWorld().equals(activeGame.getAssociatedWorld())) {
            return;
        }

        if (player.getGameMode() == GameMode.SPECTATOR) {
            final Entity e = event.getRightClicked();
            final EntityType type = e.getType();
            if (type == EntityType.PLAYER || type == EntityType.VILLAGER) {
                event.setCancelled(true);
            }
        }
    }
}
