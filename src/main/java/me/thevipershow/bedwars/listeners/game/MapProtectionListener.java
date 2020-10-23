package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.bedwars.game.AbstractActiveMerchant;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveSpawner;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public final class MapProtectionListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public MapProtectionListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private boolean isBlockNearSpawner(final Block block) {
        for (final ActiveSpawner activeSpawner : activeGame.getActiveSpawners())
            if (activeSpawner.getSpawner().getSpawnPosition().squaredDistance(block.getLocation()) <= 12.0)
                return true;
        return false;
    }

    private boolean isBlockInsideSpawn(final Block block) {
        for (final TeamSpawnPosition spawn : activeGame.getBedwarsGame().getMapSpawns())
            if (spawn.squaredDistance(block.getLocation()) < 16.01)
                return true;
        return false;
    }

    private boolean isBlockInsideMerchant(final Block block) { //TODO: Implement
        for (AbstractActiveMerchant activeMerchant : activeGame.getActiveMerchants()) {
            if (activeMerchant.getMerchant().getMerchantPosition().squaredDistance(block.getLocation()) < 16.01) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public final void onPlayerInteractAtEntity(final PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() != null && event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public final void onPlayerDeath(final BlockPlaceEvent event) {
        final Block block = event.getBlock();

        if (!activeGame.isHasStarted()) {
            return;
        }

        if (!block.getWorld().equals(activeGame.getAssociatedWorld())) return;

        if (isBlockNearSpawner(block) || isBlockInsideSpawn(block) || isBlockInsideMerchant(block)) {
            event.setCancelled(true);
        } else {
            activeGame.getPlayerPlacedBlocks().add(block);
        }
    }

}
