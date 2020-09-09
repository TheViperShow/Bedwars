package me.thevipershow.aussiebedwars.game.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.ActiveSpawner;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class SoloActiveGame extends ActiveGame {

    public SoloActiveGame(String associatedWorldFilename, BedwarsGame bedwarsGame, World lobbyWorld, Plugin plugin) {
        super(associatedWorldFilename, bedwarsGame, lobbyWorld, plugin);
    }

    @Override
    public void start() {
        if (associatedQueue.queueSize() >= bedwarsGame.getMinGames()) {
            if (associatedWorld == null) {
                handleError("Something went wrong while you were being sent into the game.");
                return;
            }
            assignTeams(); // putting each player in a different team in the map .
            assignScoreboards(); // starting and assiging a scoreboard for each player. //TODO: FINISH (DONE) - CHECK
            createSpawners(); // creating and spawning ore spawners for this map. //TODO: FINISH
            createMerchants(); // creating and spawning merchants for this map. //TODO: FINISH
            moveTeamsToSpawns(); // moving everyone to their team's spawn.

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
                    .ifPresent(spawnPos -> p.teleport(spawnPos.toLocation(associatedWorld)));
        });
    }

    @Override
    public void stop() {

    }

    @Override
    public void declareWinner() {

    }

    @Override
    public void assignTeams() {
        final Iterator<BedwarsTeam> loadedTeams = bedwarsGame.getTeams().iterator();
        associatedQueue.perform(p -> {
            if (loadedTeams.hasNext()) {
                super.assignedTeams.put(loadedTeams.next(), Collections.singleton(p));
            }
        });
    }

    @Override
    public void assignScoreboards() {
        for (Map.Entry<BedwarsTeam, Set<Player>> entry : assignedTeams.entrySet()) {
            final BedwarsTeam team = entry.getKey();
            final Player p = entry.getValue().stream().findAny().get();
            final Scoreboard scoreboard = ScoreboardLib.createScoreboard(p);

            scoreboard.setHandler(new ScoreboardHandler() {

                @Override
                public String getTitle(final Player player) {
                    return AussieBedwars.PREFIX;
                }

                @Override
                public List<Entry> getEntries(final Player player) {
                    EntryBuilder builder = new EntryBuilder();
                    for (BedwarsTeam t : assignedTeams.keySet()) {
                        builder.next(" §7Team " + t.getColorCode() + t.name() + getTeamChar(t));
                    }
                    return builder.build();

                }
            }).setUpdateInterval(20L);

            scoreboard.activate();
            super.activeScoreboards.add(scoreboard);
        }
    }

    @Override
    public void createSpawners() {
        getActiveSpawners().forEach(ActiveSpawner::spawn);
    }

    @Override
    public void createMerchants() {

    }

    @Override
    public void destroyTeamBed(BedwarsTeam team) {

    }
}
