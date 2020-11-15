package me.thevipershow.bedwars.game.objects;

import java.util.List;
import java.util.Set;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.bedwars.events.TeamBedDestroyEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.material.Bed;

public final class BedDestroyer {

    private final ActiveGame activeGame;

    public BedDestroyer(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private static void breakBed(Block block) {
        Bed bed = (Bed) block.getState().getData();
        Block relative = block.getRelative(bed.getFacing());
        if (bed.isHeadOfBed()) {
            breakOrdered(block, relative);
        } else {
            breakOrdered(relative, block);
        }
        cleanNearbyBeds(block.getLocation());
    }

    private static void breakOrdered(Block bedHead, Block bedFoot) {
        final Material air = Material.AIR;
        bedHead.setType(air);
        bedFoot.setType(air);
    }

    private static void cleanNearbyBeds(Location loc) {
        loc.getWorld().getNearbyEntities(loc, 3.0, 2.0, 3.0)
                .stream()
                .filter(i -> i.getType() == EntityType.DROPPED_ITEM && ((Item) i).getItemStack().getType() == Material.BED)
                .forEach(Entity::remove);
    }

    public final void destroyBed(BedwarsTeam team) {
        final List<TeamSpawnPosition> list = activeGame.getBedwarsGame().getBedSpawnPositions();
        for (TeamSpawnPosition teamSpawnPosition : list) {
            if (team == teamSpawnPosition.getBedwarsTeam()) {
                World gameWorld = activeGame.getCachedGameData().getGame();
                Block block = gameWorld.getBlockAt(teamSpawnPosition.toLocation(gameWorld));
                if (block.getType() == Material.BED_BLOCK) {
                    breakBed(block);
                }
                return;
            }
        }
    }

    public final void destroyInactiveBeds() {
        final List<BedwarsTeam> teams = activeGame.getBedwarsGame().getTeams();
        final Set<BedwarsTeam> loaded = activeGame.getTeamManager().getDataMap().keySet();
        for (BedwarsTeam team : teams) {
            boolean exists = false;
            for (BedwarsTeam value : loaded) {
                if (value == team) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                destroyBed(team);
            }
        }
    }
}
