package me.thevipershow.bedwars.game.objects;

import lombok.RequiredArgsConstructor;
import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.bedwars.game.ActiveGame;

@RequiredArgsConstructor
public final class MovementsManager {

    private final ActiveGame activeGame;

    public final void moveAllSpawn() {
        for (BedwarsPlayer gamePlayer : activeGame.getTeamManager().getGamePlayers()) {
            gamePlayer.teleport(activeGame.getCachedGameData().getCachedServerSpawnLocation());
        }
    }

    public final void moveToSpawnpoints() {
        for (TeamSpawnPosition teamSpawnPosition : activeGame.getBedwarsGame().getMapSpawns()) {
            for (BedwarsPlayer gamePlayer : activeGame.getTeamManager().getGamePlayers()) {
                if (gamePlayer.getBedwarsTeam() == teamSpawnPosition.getBedwarsTeam()) {
                    gamePlayer.teleport(teamSpawnPosition.toLocation(activeGame.getCachedGameData().getGame()));
                }
            }
        }
    }
}
