package me.thevipershow.bedwars.game.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import me.thevipershow.bedwars.game.ActiveGame;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class SoloActiveGame extends ActiveGame {

    public SoloActiveGame(String associatedWorldFilename, BedwarsGame bedwarsGame, World lobbyWorld, Plugin plugin) {
        super(associatedWorldFilename, bedwarsGame, lobbyWorld, plugin);
    }

    @Override
    public void moveTeamsToSpawns() {
        super.assignedTeams.forEach((k, v) -> {
            final Player p = v.stream().findAny().get();
            super.bedwarsGame.getMapSpawns()
                    .stream()
                    .filter(pos -> pos.getBedwarsTeam() == k)
                    .findAny()
                    .ifPresent(spawnPos -> {
                        p.teleport(spawnPos.toLocation(associatedWorld));
                        p.setGameMode(GameMode.SURVIVAL);
                    });
        });
    }

    @Override
    public void assignTeams() {
        final List<BedwarsTeam> teams = bedwarsGame.getTeams();
        Collections.shuffle(teams);
        final Iterator<BedwarsTeam> loadedTeams = teams.iterator();
        associatedQueue.perform(p -> {
            if (loadedTeams.hasNext()) {
                assignedTeams.put(loadedTeams.next(), Collections.singletonList(p));
            }
        });
    }

    @Override
    public void assignScoreboards() {
        for (Map.Entry<BedwarsTeam, List<Player>> entry : assignedTeams.entrySet()) {
            final Player p = entry.getValue().stream().findAny().get();
            final Scoreboard scoreboard = ScoreboardLib.createScoreboard(p);
            scoreboard.setHandler(super.scoreboardHandler).setUpdateInterval(20L);
            super.activeScoreboards.put(p, scoreboard);
            scoreboard.activate();
        }
    }

}
