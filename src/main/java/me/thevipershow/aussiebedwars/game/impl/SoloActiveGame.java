package me.thevipershow.aussiebedwars.game.impl;

import java.util.Collections;
import java.util.Iterator;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.game.ActiveGame;
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
            assignScoreboards(); // starting and assiging a scoreboard for each player.
            createSpawners(); // creating and spawning ore spawners for this map.
            createMerchants(); // creating and spawning merchants for this map.

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

    }

    @Override
    public void createSpawners() {

    }

    @Override
    public void createMerchants() {

    }
}
