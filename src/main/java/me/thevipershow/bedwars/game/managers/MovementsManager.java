package me.thevipershow.bedwars.game.managers;

import java.util.Map;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import org.bukkit.entity.Player;

public final class MovementsManager {

    public MovementsManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private final ActiveGame activeGame;

    public final void moveAllSpawn() {
        for (BedwarsPlayer bedwarsPlayer : activeGame.getInternalGameManager().getPlayerMapper().getMappings().values()) {
            bedwarsPlayer.teleport(activeGame.getCachedGameData().getCachedServerSpawnLocation());
        }
    }

    public final void moveToSpawn(Player player) {
        player.teleport(activeGame.getCachedGameData().getCachedServerSpawnLocation());
    }

    public final void moveToSpawnpoints() {
        for (Map.Entry<BedwarsTeam, ? extends TeamData<?>> entry : activeGame.getTeamManager().getDataMap().entrySet()) {
            for (TeamSpawnPosition spawn : activeGame.getBedwarsGame().getMapSpawns()) {
                if (spawn.getBedwarsTeam() == entry.getKey()) {
                    entry.getValue().perform(bedwarsPlayer -> bedwarsPlayer.teleport(spawn.toLocation(activeGame.getCachedGameData().getGame())));
                    break;
                }
            }
        }
    }

    public final void moveToWaitingRoom(Player player) {
        player.teleport(activeGame.getCachedGameData().getCachedWaitingLocation());
    }
}
