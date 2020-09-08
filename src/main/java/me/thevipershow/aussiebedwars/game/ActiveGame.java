package me.thevipershow.aussiebedwars.game;

import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.listeners.AbstractQueue;
import me.thevipershow.aussiebedwars.listeners.MatchmakingQueue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class ActiveGame {

    protected final String associatedWorldFilename;
    protected final BedwarsGame bedwarsGame;
    protected final World associatedWorld;
    protected final World lobbyWorld;
    protected final Location cachedSpawnLocation;
    protected final Plugin plugin;
    protected final AbstractQueue<Player> associatedQueue;
    protected final Location cachedWaitingLocation;

    public ActiveGame(String associatedWorldFilename, BedwarsGame bedwarsGame, World lobbyWorld, Plugin plugin) {
        this.associatedWorldFilename = associatedWorldFilename;
        this.bedwarsGame = bedwarsGame;
        this.lobbyWorld = lobbyWorld;
        // System.out.println("Sing us a song , you're the piano man -> " + associatedWorldFilename);
        this.associatedWorld = Bukkit.getWorld(associatedWorldFilename);
        this.plugin = plugin;
        this.cachedSpawnLocation = this.lobbyWorld.getSpawnLocation();
        this.associatedQueue = new MatchmakingQueue(bedwarsGame.getPlayers());
        this.cachedWaitingLocation = bedwarsGame.getLobbySpawn().toLocation(associatedWorld);
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

    public static void connectedToQueue(final Player player, final ActiveGame activeGame) {
        player.sendMessage(AussieBedwars.PREFIX + "§eYou have joined §7" + activeGame.getAssociatedWorld().getName() + " §equeue");
        player.sendMessage(AussieBedwars.PREFIX + String.format("§eStatus §7[§a%d§8/§a%d§7]", activeGame.getAssociatedQueue().queueSize(), activeGame.getBedwarsGame().getPlayers()));
    }

    public void moveToWaitingRoom(final Player player) {
        if (cachedWaitingLocation != null) {
            if (player.teleport(cachedWaitingLocation)) {
                connectedToQueue(player, this);
            }
        } else
            player.sendMessage(AussieBedwars.PREFIX + "Something went wrong when teleporting you to waiting room.");
    }

    public abstract void assignTeams();

    public abstract void assignScoreboards();

    public abstract void createSpawners();

    public abstract void createMerchants();

    public BedwarsGame getBedwarsGame() {
        return bedwarsGame;
    }

    public String getAssociatedWorldFilename() {
        return associatedWorldFilename;
    }

    public Location getCachedWaitingLocation() {
        return cachedWaitingLocation;
    }

    public World getAssociatedWorld() {
        return associatedWorld;
    }

    public World getLobbyWorld() {
        return lobbyWorld;
    }

    public Location getCachedSpawnLocation() {
        return cachedSpawnLocation;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public AbstractQueue<Player> getAssociatedQueue() {
        return associatedQueue;
    }

    private boolean isRunning = true;

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public final String toString() {
        return "ActiveGame{" +
                "associatedWorldFilename='" + associatedWorldFilename + '\'' +
                ", bedwarsGame=" + bedwarsGame +
                ", associatedWorld=" + associatedWorld +
                ", lobbyWorld=" + lobbyWorld +
                ", cachedSpawnLocation=" + cachedSpawnLocation +
                ", plugin=" + plugin +
                ", associatedQueue=" + associatedQueue +
                ", cachedWaitingLocation=" + cachedWaitingLocation +
                ", isRunning=" + isRunning +
                '}';
    }
}
