package me.thevipershow.bedwars.listeners.game;

import java.util.List;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event) {
        final Entity entity = event.getEntity();

        if (!activeGame.isHasStarted()) {
            return;
        }

        if (!(entity instanceof TNTPrimed) && !(entity instanceof Fireball)) {
            return;
        }

        if (entity.getType() == EntityType.PRIMED_TNT) {
            activeGame.getPlugin().getServer().getScheduler()
                    .runTaskLater(activeGame.getPlugin(), () -> activeGame.getPlacedTntMap().remove(event.getEntity().getUniqueId()), 2L);
        }

        final List<Block> affectedBlocks = event.blockList();

        final List<Block> toDestroy = affectedBlocks.stream()
                .filter(block -> activeGame.getPlayerPlacedBlocks().contains(block)
                        && block.getType() != Material.GLASS
                        && block.getType() != Material.STAINED_GLASS
                        && block.getType() != Material.ENDER_STONE)
                .collect(Collectors.toList());

        if (toDestroy.size() < affectedBlocks.size()) {
            event.setCancelled(true);
            toDestroy.forEach(b -> b.setType(Material.AIR));
        }
    }
}
