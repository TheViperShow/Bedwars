package me.thevipershow.bedwars.game.objects;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;

public final class SoloTeamManager extends TeamManager {
    public SoloTeamManager(ActiveGame activeGame) {
        super(activeGame);
    }

    @Override
    public void assignTeams() {
        List<BedwarsTeam> loadedTeams = getActiveGame().getBedwarsGame().getTeams(); // Ensuring that all teams
        Collections.shuffle(loadedTeams);                                                  // are displaced randomly.

        Iterator<BedwarsTeam> teamsIterator = loadedTeams.iterator(); // Using an iterator to assign teams.

        getActiveGame().getGameLobbyTicker().getAssociatedQueue().perform(player -> { // Looping through all players in the queue
            final BedwarsPlayer bedwarsPlayer = BedwarsPlayer.from(player);           // and giving for granted that they're online.
            getGamePlayers().add(bedwarsPlayer);

            if (!teamsIterator.hasNext()) {
                throw new RuntimeException("There were more players in this queue than the gamemode's maximum amount.");
            }

            bedwarsPlayer.setBedwarsTeam(teamsIterator.next());
        });
    }
}
