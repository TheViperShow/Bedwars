package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveGameState;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public final class LobbyUnregisterableListener extends UnregisterableListener {

    public LobbyUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (activeGame.getGameState() == ActiveGameState.QUEUE && block.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public final void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (activeGame.getGameState() == ActiveGameState.QUEUE && block.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public final void onEntityDamageByEntity(EntityDamageEvent event) {
        Entity damaged = event.getEntity();
        if (activeGame.getGameState() == ActiveGameState.QUEUE && damaged.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public final void onPlayerMove(PlayerMoveEvent event) {
        final double y = event.getTo().getY();
        if (y < 0 && (activeGame.getGameState() == ActiveGameState.QUEUE && event.getTo().getWorld().equals(activeGame.getCachedGameData().getGame()))) {
            event.setCancelled(true);
            event.getPlayer().setVelocity(new Vector(0,0,0));
            event.getPlayer().teleport(activeGame.getCachedGameData().getCachedWaitingLocation());
        }
    }
}
