package me.thevipershow.bedwars.game.managers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.AbstractQueue;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.teams.impl.MultiTeamData;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import org.bukkit.entity.Player;

public final class MultiTeamManager extends TeamManager<Set<BedwarsPlayer>> {
    public MultiTeamManager(ActiveGame activeGame) {
        super(activeGame);
    }

    @Override
    public void assignTeams() {
        AbstractQueue<Player> queue = getActiveGame().getGameLobbyTicker().getAssociatedQueue();
        List<BedwarsTeam> availableTeams = getActiveGame().getBedwarsGame().getTeams(); // Shuffling the teams to ensure
        Collections.shuffle(availableTeams);                                            // equal distribution.

        Iterator<BedwarsTeam> teamIterator = availableTeams.iterator();
        Iterator<Player> playerIterator = queue.getIterator();

        final int pickPerTeam = getActiveGame().getBedwarsGame().getGamemode().getTeamPlayers();

        while (true) {
            boolean exitFromLoop = false;
            int picked = 0;
            Set<BedwarsPlayer> bedwarsPlayerSet = new HashSet<>();
            while (picked < pickPerTeam) {
                if (!playerIterator.hasNext()) {
                    exitFromLoop = true;
                    break;
                } else {
                    Player current = playerIterator.next();
                    bedwarsPlayerSet.add(getActiveGame().getPlayerMapper().get(current));
                   // bedwarsPlayerSet.add(BedwarsPlayer.from(current));
                }
                picked++;
            }
            if (!teamIterator.hasNext()) {
                throw new RuntimeException("Not enough teams for this game.");
            } else if (picked != 0) {
                BedwarsTeam team = teamIterator.next();
                TeamData<Set<BedwarsPlayer>> teamData = new MultiTeamData(getActiveGame().getBedwarsGame().getGamemode(), getActiveGame().getInternalGameManager().getPlayerMapper());
                teamData.setData(bedwarsPlayerSet);
                getDataMap().put(team, teamData);
            }

            if (exitFromLoop) {
                break;
            }
        }
    }
}
