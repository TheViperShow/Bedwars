package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

@Deprecated
public final class TNTPlaceListener extends UnregisterableListener {

    public TNTPlaceListener(ActiveGame activeGame) {
        super(activeGame);
    }

    /*
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (!activeGame.isHasStarted()) {
            return;
        }

        if (!event.getBlock().getWorld().equals(activeGame.getAssociatedWorld())) {
            return;
        }
        final Block b = event.getBlock();
        if (b.getType() == Material.TNT) {
            GameUtils.decreaseItemInHand(event.getPlayer());
            event.setCancelled(true);
            final TNTPrimed tnt = (TNTPrimed) b.getWorld().spawnEntity(b.getLocation().add(0.501, 0.05, 0.501), EntityType.PRIMED_TNT);
            tnt.setFuseTicks(activeGame.getBedwarsGame().getTntFuse());
            activeGame.getPlacedTntMap().put(tnt.getUniqueId(), event.getPlayer().getUniqueId());
        }
    }*/
}
