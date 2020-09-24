package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameUtils;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public final class TNTPlaceListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public TNTPlaceListener(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (!event.getBlock().getWorld().equals(activeGame.getAssociatedWorld())) return;
        final Block b = event.getBlock();
        if (b.getType() == Material.TNT) {
            GameUtils.decreaseItemInHand(event.getPlayer());
            event.setCancelled(true);
            b.getWorld().spawnEntity(b.getLocation().add(0.501, 0.0501, 0.501), EntityType.PRIMED_TNT);
        }
    }
}
