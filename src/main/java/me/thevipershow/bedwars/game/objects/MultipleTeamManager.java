package me.thevipershow.bedwars.game.objects;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Player;

public final class MultipleTeamManager extends TeamManager {
    public MultipleTeamManager(ActiveGame activeGame) {
        super(activeGame);
    }

    @Override
    public void assignTeams() {
        List<BedwarsTeam> loadedTeams = getActiveGame().getBedwarsGame().getTeams(); // Ensuring that all teams
        Collections.shuffle(loadedTeams);                                                  // are displaced randomly.

        Iterator<BedwarsTeam> teamsIterator = loadedTeams.iterator(); // Using an iterator to assign teams.

        Iterator<Player> playerIterator = getActiveGame().getGameLobbyTicker().getAssociatedQueue().getIterator();

        final int playerPerTeam = getActiveGame().getBedwarsGame().getGamemode().getTeamPlayers();

        teamLabel:
        while (teamsIterator.hasNext()) {
            BedwarsTeam currentTeam = teamsIterator.next();

            int picked = 0;
            while (picked < playerPerTeam) {
                if (playerIterator.hasNext()) {
                    Player currentPlayer = playerIterator.next();
                    BedwarsPlayer bedwarsPlayer = BedwarsPlayer.from(currentPlayer);
                    bedwarsPlayer.setBedwarsTeam(currentTeam);
                    getGamePlayers().add(bedwarsPlayer);
                } else {
                    break teamLabel;
                }
                picked++;
            }
        }
    }
}
