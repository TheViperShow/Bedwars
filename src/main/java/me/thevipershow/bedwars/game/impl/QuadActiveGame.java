package me.thevipershow.bedwars.game.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class QuadActiveGame extends ActiveGame {

    public QuadActiveGame(String associatedWorldFilename, BedwarsGame bedwarsGame, World lobbyWorld, Plugin plugin) {
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
        final Iterator<BedwarsTeam> teamsIterator = teams.iterator();

        final LinkedList<Player> queue = getAssociatedQueue().getInQueue();
        final Collection<Collection<Player>> splitTeams = GameUtils.redistributeEqually(queue, 4);

        splitTeams.forEach(team -> {
            if (teamsIterator.hasNext()) {
                assignedTeams.put(teamsIterator.next(), new ArrayList<>(team));
            }
        });
    }

    @Override
    public void assignScoreboards() {
        for (final Map.Entry<BedwarsTeam, List<Player>> entry : assignedTeams.entrySet()) {
            final List<Player> players = entry.getValue();
            for (final Player player : players) {
                final Scoreboard scoreboard = ScoreboardLib.createScoreboard(player);
                scoreboard.setHandler(super.scoreboardHandler).setUpdateInterval(20L);
                super.activeScoreboards.put(player, scoreboard);
                scoreboard.activate();
            }
        }
    }
}
