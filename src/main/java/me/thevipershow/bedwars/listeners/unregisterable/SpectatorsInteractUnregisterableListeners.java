package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.game.enums.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public final class SpectatorsInteractUnregisterableListeners extends UnregisterableListener {

    public SpectatorsInteractUnregisterableListeners(ActiveGame activeGame) {
        super(activeGame);
    }

    private <T extends Event & Cancellable> void cancel(final Player player, final T cancellableEvent) {
        boolean sameWorld = activeGame.getCachedGameData().getGame().equals(player.getWorld());
        if (!sameWorld) return;
        BedwarsPlayer bedwarsPlayer = activeGame.getPlayerMapper().get(player);
        if (bedwarsPlayer == null) return;
        if (bedwarsPlayer.getPlayerState() == PlayerState.DEAD) {
            cancellableEvent.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public final void onBlockBreak(BlockBreakEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onBlockPlace(BlockPlaceEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onPlayerDropItem(PlayerDropItemEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onPlayerPickupItem(PlayerPickupItemEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onPlayerInteract(PlayerInteractEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public final void onInventoryOpen(final InventoryOpenEvent event) {
        cancel((Player) event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public final void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            cancel((Player) event.getDamager(), event);
        }
    }

}
