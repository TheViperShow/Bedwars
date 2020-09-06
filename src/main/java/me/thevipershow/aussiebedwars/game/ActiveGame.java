package me.thevipershow.aussiebedwars.game;

import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.listeners.AbstractQueue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class ActiveGame {
    protected final BedwarsGame bedwarsGame;
    protected final AbstractQueue<? extends Player> associatedQueue;
    protected final Gamemode gamemode;
    protected final String lobbyWorldName;
    protected final World associatedWorld;
    protected final World lobbyWorld;
    protected final Location cachedSpawnLocation;
    protected final Plugin plugin;

    public ActiveGame(
            BedwarsGame bedwarsGame,
            AbstractQueue<? extends Player> associatedQueue,
            Gamemode gamemode,
            String lobbyWorldName,
            World associatedWorld, Plugin plugin) {
        this.bedwarsGame = bedwarsGame;
        this.associatedQueue = associatedQueue;
        this.gamemode = gamemode;
        this.lobbyWorldName = lobbyWorldName;
        this.associatedWorld = associatedWorld;
        this.plugin = plugin;
        this.lobbyWorld = Bukkit.getWorld(this.lobbyWorldName);
        if (lobbyWorld == null)
            throw new UnsupportedOperationException("World " + lobbyWorldName + " does not exist. Please correct config.yml");
        this.cachedSpawnLocation = this.lobbyWorld.getSpawnLocation();
    }

    public void handleError(String text) {
        associatedQueue.perform(p -> p.sendMessage(AussieBedwars.PREFIX + "§c" + text));
    }

    public abstract void start();

    public abstract void stop();

    public void moveToLobby(Player player) {
        player.teleport(cachedSpawnLocation);
    }

    public void moveAllToLobby() {
        associatedQueue.performAndClean(player -> {
            if (player.isOnline())
                player.teleport(cachedSpawnLocation);
        });
    }

    public abstract void moveToWaitingRoom();

    public abstract void assignTeams();

    public abstract void assignScoreboards();

    public abstract void createSpawners();

    public abstract void createMerchants();


    private boolean isRunning = true;

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
