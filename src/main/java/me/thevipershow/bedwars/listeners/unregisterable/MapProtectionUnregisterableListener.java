package me.thevipershow.bedwars.listeners.unregisterable;

import java.util.HashSet;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.bedwars.events.TeamBedDestroyEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.objects.BedwarsPlayer;
import me.thevipershow.bedwars.game.objects.CachedGameData;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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

    private boolean isOwnBed(Player player, Block block) {
        BedwarsPlayer bedwarsPlayer = activeGame.getPlayerMapper().get(player);
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
        }

        System.out.println(returnValue);
        return returnValue;
    }

    @EventHandler(ignoreCancelled = true)
    public final void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block broken = event.getBlock();
        Material type = broken.getType();

        if (type == Material.BED_BLOCK) {
            if (isOwnBed(player, broken)) {
                event.setCancelled(true);
                player.sendMessage(AllStrings.PREFIX.get() + AllStrings.CANNOT_BREAK_OWN_BED.get());
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public final void onBlockPlace(BlockPlaceEvent event) {
        cachedGameData.getCachedPlacedBlocks().add(event.getBlock());
    }
}
