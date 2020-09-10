package me.thevipershow.aussiebedwars.listeners.game;

import java.util.HashSet;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.ActiveSpawner;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public final class MapProtectionListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    private final HashSet<Block> playerPlacedBlocks = new HashSet<>();

    public MapProtectionListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private boolean isBlockNearSpawner(final Block block) {
        for (final ActiveSpawner activeSpawner : activeGame.getActiveSpawners())
            if (activeSpawner.getSpawner().getSpawnPosition().squaredDistance(block.getLocation()) <= 2.00)
                return true;
        return false;
    }

    private boolean isBlockInsideSpawn(final Block block) {
        for (final TeamSpawnPosition spawn : activeGame.getBedwarsGame().getMapSpawns())
            if (spawn.squaredDistance(block.getLocation()) <= 4.00)
                return true;
        return false;
    }

    private boolean isBlockInsideMerchant(final Block block) { //TODO: Implement
        return false;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(final BlockPlaceEvent event) {
        final Block block = event.getBlock();
        if (!block.getWorld().equals(activeGame.getAssociatedWorld())) return;

        if (isBlockNearSpawner(block) || isBlockInsideSpawn(block)) {
            event.setCancelled(true);
        } else {
            playerPlacedBlocks.add(block);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        final Player p = event.getPlayer();
        final Block block = event.getBlock();
        if (!block.getWorld().equals(activeGame.getAssociatedWorld())) return;

        if (playerPlacedBlocks.contains(block)) {
            playerPlacedBlocks.remove(block);
        } else {
            event.setCancelled(true);
        }
    }
}
