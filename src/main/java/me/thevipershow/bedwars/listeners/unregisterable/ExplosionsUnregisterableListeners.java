package me.thevipershow.bedwars.listeners.unregisterable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.CachedGameData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import static org.bukkit.block.BlockFace.DOWN;
import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.UP;
import static org.bukkit.block.BlockFace.WEST;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

public final class ExplosionsUnregisterableListeners extends UnregisterableListener {

    public ExplosionsUnregisterableListeners(ActiveGame activeGame) {
        super(activeGame);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onExplosionPrime(EntityExplodeEvent event) {
        Entity exploding = event.getEntity();

        if (!exploding.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            return;
        }

        CachedGameData cachedGameData = activeGame.getCachedGameData();
        HashSet<Block> placedBlocks = cachedGameData.getCachedPlacedBlocks();

        removeAllDestroyable(event.blockList(), placedBlocks, exploding.getLocation());

        event.setCancelled(true);
    }

    private static Collection<Block> getDestroyableBlocks(Location explosionSource, Collection<Block> toDestroy, Collection<Block> canBeDestroyed) {
        final Set<Block> destroyable = new HashSet<>();
        for (Block block : toDestroy) {
            if (canBeDestroyed.contains(block) && !isProtectionMaterial(block) && !protectedByGlass(block, explosionSource)) {
                destroyable.add(block);
            }
        }
        return destroyable;
    }

    private static final BlockFace[] protections = {WEST, EAST, NORTH, SOUTH, UP, DOWN};
    private static final Material[] protectionMaterials = {Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.GLASS, Material.ENDER_STONE};
    private static final float SQRT_HALF_ONE = 0.707106f;

    private static boolean isProtectionMaterial(Block block) {
        Material type = block.getType();
        for (Material material : protectionMaterials) {
            if (type == material) {
                return true;
            }
        }
        return false;
    }

    private static boolean intersection(Location explosion, Location protectionBlock, Location target) {
        World world = explosion.getWorld();
        Block pBlock = world.getBlockAt(protectionBlock);
        Vector vector = new Vector(
                (target.getX() - explosion.getX()),
                (target.getY() - explosion.getY()),
                (target.getZ() - explosion.getZ())).normalize();

        for (int i = 0; i < 6; i++) {
            Block block = world.getBlockAt(explosion.add(vector));
            if (block.equals(pBlock)) {
                return true;
            }
        }

        return false;
    }

    private static boolean protectedByGlass(Block exploded, Location explosionSource) {
        World world = exploded.getWorld();
        Location explodedLocation = exploded.getLocation();

        for (BlockFace protectionFace : protections) {
            Block relative = exploded.getRelative(protectionFace);
            if (isProtectionMaterial(relative)) {
                if (explodedLocation.distanceSquared(relative.getLocation()) > 1 && intersection(explosionSource, relative.getLocation(), explodedLocation)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static void removeAllDestroyable(Collection<Block> toDestroy, Collection<Block> canBeDestroyed, Location explosionSource) {
        for (Block block : getDestroyableBlocks(explosionSource, toDestroy, canBeDestroyed)) {
            block.setType(Material.AIR);
        }
    }
}
