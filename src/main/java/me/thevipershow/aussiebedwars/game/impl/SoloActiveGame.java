package me.thevipershow.aussiebedwars.game.impl;

import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.AbstractQueue;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SoloActiveGame extends ActiveGame {

    public SoloActiveGame(
            BedwarsGame bedwarsGame,
            AbstractQueue<? extends Player> associatedQueue,
            String lobbyWorldName,
            World associatedWorld,
            Plugin plugin) {
        super(bedwarsGame, associatedQueue, Gamemode.SOLO, lobbyWorldName, associatedWorld, plugin);
    }

    @Override
    public void start() {
        if (associatedQueue.queueSize() >= bedwarsGame.getMinGames()) {
            if (associatedWorld == null) {
                handleError("Something went wrong while you were being sent into the game.");
                return;
            }

            this.assignTeams();
            this.assignScoreboards();
            this.createSpawners();
            this.createMerchants();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void moveToWaitingRoom() {
        // associatedQueue.perform(player -> player.teleport());
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
