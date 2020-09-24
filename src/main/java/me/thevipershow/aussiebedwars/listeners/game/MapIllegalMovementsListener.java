package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.config.objects.SpawnPosition;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.ActiveSpawner;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public final class MapIllegalMovementsListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    private final double[][] bounds;
    // [x][0\1] -> These are the min. and max. map x axis bounds (first min, second max)
    // [y][0\1] -> These are the min. and max. map y axis bounds (first min, second max)
    // [z][0\1] -> These are the min. and max. map z axis bounds (first min, second max)

    private static double[][] genBounds(final ActiveGame game) {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ = Double.MIN_VALUE;

        final double[][] data = new double[3][2];

        for (final ActiveSpawner spawner : game.getActiveSpawners()) {
            final SpawnPosition pos = spawner.getSpawner().getSpawnPosition();

            final double tempX = pos.getX();
            final double tempZ = pos.getZ();

            if (tempX > maxX)
                maxX = tempX;
            if (tempX < minX)
                minX = tempX;
            if (tempZ > maxZ)
                maxZ = tempZ;
            if (tempZ < minZ)
                minZ = tempZ;
        }

        data[0] = new double[]{minX - 50, maxX + 50};
        data[1] = new double[]{0, 120};
        data[2] = new double[]{minZ - 50, maxZ + 50};

        return data;
    }

    private boolean isOutOfBounds(final Location loc, final boolean considerY) {
        final double x = loc.getX();
        final double y = loc.getY();
        final double z = loc.getZ();

        return (x < bounds[0][0] || x > bounds[0][1]) ||
                (y < bounds[1][0] || (considerY && y > bounds[1][1])) ||
                (z < bounds[2][0] || z > bounds[2][1]);
    }

    private boolean isOutOfBounds(final Block block) {
        return isOutOfBounds(block.getLocation(), true);
    }

    private boolean isOutOfBounds(final Player player) {
        return isOutOfBounds(player.getLocation(), true);
    }

    public MapIllegalMovementsListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
        this.bounds = genBounds(activeGame);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (!activeGame.getAssociatedWorld().equals(player.getWorld())) return;
        if (!activeGame.isHasStarted()) return;
        if (isOutOfBounds(player)) {
            if (activeGame.isOutOfGame(player)) {
                event.setCancelled(true);
            } else {
                final EntityDamageEvent e = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.VOID, 20.00);
                activeGame.getPlugin().getServer().getPluginManager().callEvent(e);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlock();
        final Player player = event.getPlayer();
        if (player.getWorld().equals(activeGame.getAssociatedWorld()) && isOutOfBounds(block)) {
            event.setCancelled(true);
        }
    }
}
