package me.thevipershow.bedwars.game.managers;

import java.util.List;
import java.util.Set;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.enums.TeamStatus;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.material.Bed;

public final class BedManager {

    private final ActiveGame activeGame;

    public BedManager(ActiveGame activeGame) {
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

    public static void breakOrdered(Block bedHead, Block bedFoot) {
        final Material air = Material.AIR;
        bedHead.setType(air);
        bedFoot.setType(air);
    }

    public static void cleanNearbyBeds(Location loc) {
        loc.getWorld().getNearbyEntities(loc, 3.0, 2.5, 3.0)
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
        TeamManager<?> teamManager = activeGame.getTeamManager();
        TeamData<?> data = teamManager.dataOfTeam(team);
        if (data != null) {
            data.setStatus(TeamStatus.BED_BROKEN);
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
