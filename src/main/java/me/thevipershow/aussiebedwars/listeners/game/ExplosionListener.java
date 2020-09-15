package me.thevipershow.aussiebedwars.listeners.game;

import java.util.List;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosionListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public ExplosionListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityExplode(final EntityExplodeEvent event) {
        final Entity entity = event.getEntity();
        if (!(entity instanceof TNTPrimed) && !(entity instanceof Fireball)) return;

        final List<Block> affectedBlocks = event.blockList();
        final List<Block> toDestroy = affectedBlocks.stream()
                .filter(block -> activeGame.getPlayerPlacedBlocks().contains(block))
                .collect(Collectors.toList());
        if (toDestroy.size() != affectedBlocks.size()) {
            event.setCancelled(true);
            toDestroy.forEach(b -> b.setType(Material.AIR));
        }
    }
}
