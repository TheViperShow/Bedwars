package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.aussiebedwars.game.AbstractActiveMerchant;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.ActiveSpawner;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
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
            if (activeSpawner.getSpawner().getSpawnPosition().squaredDistance(block.getLocation()) <= 7.5)
                return true;
        return false;
    }

    private boolean isBlockInsideSpawn(final Block block) {
        for (final TeamSpawnPosition spawn : activeGame.getBedwarsGame().getMapSpawns())
            if (spawn.squaredDistance(block.getLocation()) < 16.00)
                return true;
        return false;
    }

    private boolean isBlockInsideMerchant(final Block block) { //TODO: Implement
        for (AbstractActiveMerchant activeMerchant : activeGame.getActiveMerchants()) {
            if (activeMerchant.getMerchant().getMerchantPosition().squaredDistance(block.getLocation()) < 16.00) {
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerDeath(final BlockPlaceEvent event) {
        final Block block = event.getBlock();
        if (!block.getWorld().equals(activeGame.getAssociatedWorld())) return;

        if (isBlockNearSpawner(block) || isBlockInsideSpawn(block) || isBlockInsideMerchant(block)) {
            event.setCancelled(true);
        } else {
            activeGame.getPlayerPlacedBlocks().add(block);
        }
    }

}
