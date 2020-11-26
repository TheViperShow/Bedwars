package me.thevipershow.bedwars.listeners.unregisterable;

import java.util.HashSet;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.api.TeamBedDestroyEvent;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.SpawnPosition;
import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.game.CachedGameData;
import me.thevipershow.bedwars.game.managers.BedManager;
import me.thevipershow.bedwars.game.spawners.ActiveSpawner;
import me.thevipershow.bedwars.game.upgrades.merchants.AbstractActiveMerchant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Bed;

public final class MapProtectionUnregisterableListener extends UnregisterableListener {
    private final CachedGameData cachedGameData;

    public MapProtectionUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
        this.cachedGameData = activeGame.getCachedGameData();
    }

    /**
     * This method checks if the given {@link BedwarsPlayer} has broke a bed that he owned.
     * Each team has a bed, players should not be able to break their own bed.
     *
     * @param bedwarsPlayer The BedwarsPlayer who has broken the block.
     * @param block         The Block broken.
     * @return True if the bed was his own, false otherwise.
     */
    private boolean isOwnBed(BedwarsPlayer bedwarsPlayer, Block block) {
        if (bedwarsPlayer == null) {
            return true;
        }

        BedwarsTeam team = bedwarsPlayer.getBedwarsTeam();

        Bed data = (Bed) block.getState().getData();
        Block relative = block.getRelative(data.getFacing());

        BedwarsTeam destroyedBedTeamOwner = null;
        for (TeamSpawnPosition bedSpawnPosition : activeGame.getBedwarsGame().getBedSpawnPositions()) {
            World gameWorld = activeGame.getCachedGameData().getGame();
            Location bedLocation = bedSpawnPosition.toLocation(gameWorld);
            Block blockAt = gameWorld.getBlockAt(bedLocation);
            if (blockAt.equals(block) || blockAt.equals(relative)) {
                destroyedBedTeamOwner = bedSpawnPosition.getBedwarsTeam();
                break;
            }
        }

        final boolean returnValue = destroyedBedTeamOwner == team;

        if (!returnValue) {
            TeamBedDestroyEvent destroyEvent = new TeamBedDestroyEvent(activeGame, destroyedBedTeamOwner, bedwarsPlayer);
            activeGame.getPlugin().getServer().getPluginManager().callEvent(destroyEvent);

            if (!destroyEvent.isCancelled()) {
                BedManager.cleanNearbyBeds(block.getLocation(), activeGame.getPlugin());
            }
        }

        return returnValue;
    }

    @EventHandler(ignoreCancelled = true)
    public final void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            return;
        }
        BedwarsPlayer bedwarsPlayer = activeGame.getPlayerMapper().get(player);
        if (bedwarsPlayer == null) {
            return;
        }
        Block broken = event.getBlock();
        Material type = broken.getType();

        if (type == Material.BED_BLOCK) {
            if (isOwnBed(bedwarsPlayer, broken)) {
                event.setCancelled(true);
                player.sendMessage(AllStrings.PREFIX.get() + AllStrings.CANNOT_BREAK_OWN_BED.get());
            } else {
              BedManager.cleanNearbyBeds(event.getBlock().getLocation(), activeGame.getPlugin());
            }
        } else {
            HashSet<Block> valid = cachedGameData.getCachedPlacedBlocks();
            if (valid.contains(broken)) {
                valid.remove(broken);
            } else {
                event.setCancelled(true);
            }
        }
    }

    private boolean isBlockNearSpawner(Block block) {
        for (final ActiveSpawner activeSpawner : activeGame.getActiveSpawnersManager().getActiveSpawners())
            if (activeSpawner.getSpawner().getSpawnPosition().squaredDistance(block.getLocation()) <= 27.0) {
                return true;
            }
        return false;
    }

    private boolean isBlockInsideSpawn(Block block) {
        final Location blockLocation = block.getLocation();
        final SpawnPosition spawnProtection = activeGame.getBedwarsGame().getSpawnProtection();
        for (final TeamSpawnPosition spawn : activeGame.getBedwarsGame().getMapSpawns()) {
            final double dX = spawn.xDistance(blockLocation);
            final double dY = spawn.yDistance(blockLocation);
            final double dZ = spawn.zDistance(blockLocation);
            if (dX <= spawnProtection.getX() && dZ <= spawnProtection.getZ() && dY <= spawnProtection.getY()) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlockInsideMerchant(Block block) {
        for (AbstractActiveMerchant activeMerchant : activeGame.getMerchantManager().getActiveMerchants()) {
            if (activeMerchant.getMerchant().getMerchantPosition().squaredDistance(block.getLocation()) < 15.00) {
                return true;
            }
        }
        return false;
    }


    public final boolean isValidPlacement(Block block) {
        return !isBlockInsideMerchant(block) && !isBlockInsideSpawn(block) && !isBlockNearSpawner(block);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public final void onBlockPlace(BlockPlaceEvent event) {
        final Block block = event.getBlockPlaced();
        if (block.getType() == Material.TNT) {
            event.setCancelled(true);
            spawnTNT(event.getPlayer(), block);
        } else {
            if (!isValidPlacement(block)) {
                event.setCancelled(true);
            } else {
                cachedGameData.getCachedPlacedBlocks().add(block);
            }
        }
    }

    /**
     * Used to spawn a TNT where the player placed a TNT block.
     * This method also handles item removal and scheduling.
     *
     * @param whoPlaced   The Player who placed the block.
     * @param blockPlaced The block placed.
     */
    private void spawnTNT(Player whoPlaced, Block blockPlaced) {
        whoPlaced.getWorld().spawn(blockPlaced.getLocation().add(0.5, 0.05, 0.5), TNTPrimed.class);
        GameUtils.decreaseItemInHand(whoPlaced);
    }
}
