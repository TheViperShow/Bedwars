package me.thevipershow.aussiebedwars.game.impl;

import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class SoloActiveGame extends ActiveGame {

    public SoloActiveGame(String associatedWorldFilename,
                          BedwarsGame bedwarsGame,
                          World lobbyWorld,
                          Plugin plugin) {
        super(associatedWorldFilename, bedwarsGame, lobbyWorld, plugin);
    }

    @Override
    public void start() {
        if (associatedQueue.queueSize() >= bedwarsGame.getMinGames()) {
            if (associatedWorld == null) {
                handleError("Something went wrong while you were being sent into the game.");
                return;
            }
            assignTeams();
            assignScoreboards();
            createSpawners();
            createMerchants();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void assignTeams() {

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
