package me.thevipershow.aussiebedwars.game.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.game.ArmorSet;
import me.thevipershow.aussiebedwars.listeners.game.Tools;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class SoloActiveGame extends ActiveGame {

    public SoloActiveGame(String associatedWorldFilename, BedwarsGame bedwarsGame, World lobbyWorld, Plugin plugin) {
        super(associatedWorldFilename, bedwarsGame, lobbyWorld, plugin);
    }

    @Override
    public void start() {
        setHasStarted(true);
        if (associatedQueue.queueSize() >= bedwarsGame.getMinPlayers()) {
            if (associatedWorld == null) {
                handleError("Something went wrong while you were being sent into the game.");
                return;
            }
            assignTeams(); // putting each player in a different team in the map .
            assignScoreboards(); // starting and assiging a scoreboard for each player.
            createSpawners(); // creating and spawning ore spawners for this map.
            createMerchants(); // creating and spawning merchants for this map.
            moveTeamsToSpawns(); // moving everyone to their team's spawn.
            giveAllDefaultSet();
            healAll();
        }
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
    public void stop() {
        moveAllToLobby();
        cleanAllAndFinalize();
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

            super.activeScoreboards.add(scoreboard);
            scoreboard.activate();
        }
    }

}
