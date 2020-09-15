package me.thevipershow.aussiebedwars.game.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class DuoActiveGame extends ActiveGame {
    public DuoActiveGame(String associatedWorldFilename, BedwarsGame bedwarsGame, World lobbyWorld, Plugin plugin) {
        super(associatedWorldFilename, bedwarsGame, lobbyWorld, plugin);
    }

    @Override
    public void moveTeamsToSpawns() {
        assignedTeams.forEach((k, v) -> bedwarsGame.getMapSpawns()
                .stream()
                .filter(pos -> pos.getBedwarsTeam() == k)
                .findAny()
                .ifPresent(spawn -> v.forEach(p -> {
                    p.teleport(spawn.toLocation(associatedWorld));
                    p.setGameMode(GameMode.SURVIVAL);
                })));
    }

    @Override
    public void assignTeams() {
        final List<BedwarsTeam> teams = bedwarsGame.getTeams();
        Collections.shuffle(teams);
        final Iterator<Player> availablePlayers = associatedQueue.getIterator();
        final Iterator<BedwarsTeam> teamsIterator = teams.iterator();

        label:
        while (teamsIterator.hasNext()) {
            final BedwarsTeam currentTeam = teamsIterator.next();
            int picked = 0;
            final List<Player> pickedPlayers = new ArrayList<>(2);
            while (availablePlayers.hasNext() && !(picked > 2)) {
                final Player player = availablePlayers.next();
                pickedPlayers.add(player);
                picked++;
                if (!availablePlayers.hasNext()) {
                    assignedTeams.put(currentTeam, pickedPlayers);
                    break label;
                }
            }
            assignedTeams.put(currentTeam, pickedPlayers);
        }

    }

    @Override
    public void assignScoreboards() {
        for (final Map.Entry<BedwarsTeam, List<Player>> entry : assignedTeams.entrySet()) {
            final List<Player> players = entry.getValue();
            for (final Player player : players) {
                final Scoreboard scoreboard = ScoreboardLib.createScoreboard(player);
                scoreboard.setHandler(super.scoreboardHandler).setUpdateInterval(20L);
                super.activeScoreboards.add(scoreboard);
                scoreboard.activate();
            }
        }
    }
}
