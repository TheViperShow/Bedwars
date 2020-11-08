package me.thevipershow.bedwars.game.objects;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.AbstractQueue;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Player;

public final class SoloTeamManager extends TeamManager<BedwarsPlayer> {
    public SoloTeamManager(ActiveGame activeGame) {
        super(activeGame);
    }

    @Override
    public final void assignTeams() {
        AbstractQueue<Player> queue = getActiveGame().getGameLobbyTicker().getAssociatedQueue();
        List<BedwarsTeam> availableTeams = getActiveGame().getBedwarsGame().getTeams(); // Shuffling the teams to ensure
        Collections.shuffle(availableTeams);                                            // equal distribution.

        Iterator<BedwarsTeam> teamIterator = availableTeams.iterator();
        Iterator<Player> playerIterator = queue.getIterator();

        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();
            if (!teamIterator.hasNext()) {
                throw new RuntimeException("Not enough teams for this game.");
            } else {
                TeamData<BedwarsPlayer> teamData = new SoloTeamData(Gamemode.SOLO, getActiveGame().getInternalGameManager().getPlayerMapper());
                teamData.add(player);
                super.getDataMap().put(teamIterator.next(), teamData);
            }
        }
    }
}
