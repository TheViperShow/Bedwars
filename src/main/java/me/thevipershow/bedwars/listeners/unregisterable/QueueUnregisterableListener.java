package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

public final class QueueUnregisterableListener extends UnregisterableListener {

    public QueueUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!activeGame.isHasStarted() && block.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public final void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (!activeGame.isHasStarted() && block.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public final void onEntityDamageByEntity(EntityDamageEvent event) {
        Entity damaged = event.getEntity();
        if (!activeGame.isHasStarted() && damaged.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public final void onPlayerMove(PlayerMoveEvent event) {
        final double y = event.getTo().getY();
        if (y < 0.0 && (!activeGame.isHasStarted() && event.getTo().getWorld().equals(activeGame.getCachedGameData().getGame()))) {
            event.setCancelled(true);
            event.getPlayer().teleport(activeGame.getCachedGameData().getCachedWaitingLocation());
        }
    }
}
