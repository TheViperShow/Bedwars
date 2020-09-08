package me.thevipershow.aussiebedwars.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.events.GameStartEvent;
import me.thevipershow.aussiebedwars.listeners.AbstractQueue;
import me.thevipershow.aussiebedwars.listeners.MatchmakingQueue;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class ActiveGame {

    protected final String associatedWorldFilename;
    protected final BedwarsGame bedwarsGame;
    protected final World associatedWorld;
    protected final World lobbyWorld;
    protected final Location cachedSpawnLocation;
    protected final Plugin plugin;
    protected final AbstractQueue<Player> associatedQueue;
    protected final Location cachedWaitingLocation;
    protected final Map<BedwarsTeam, Set<Player>> assignedTeams;

    ///////////////////////////////////////////////////
    // Internal ActiveGame fields                    //
    //-----------------------------------------------//
    //                                               //
    protected boolean hasStarted = false;            //
    protected long missingtime;                      //
    protected BukkitTask timerTask = null;           //
    //-----------------------------------------------//

    public ActiveGame(String associatedWorldFilename, BedwarsGame bedwarsGame, World lobbyWorld, Plugin plugin) {
        this.associatedWorldFilename = associatedWorldFilename;
        this.bedwarsGame = bedwarsGame;
        this.lobbyWorld = lobbyWorld;
        this.associatedWorld = Bukkit.getWorld(associatedWorldFilename);
        this.plugin = plugin;
        this.cachedSpawnLocation = this.lobbyWorld.getSpawnLocation();
        this.associatedQueue = new MatchmakingQueue(bedwarsGame.getPlayers());
        this.cachedWaitingLocation = bedwarsGame.getLobbySpawn().toLocation(associatedWorld);
        this.missingtime = bedwarsGame.getStartTimer();
        this.assignedTeams = new HashMap<>();
        tickTimer();
    }

    public void handleError(String text) {
        associatedQueue.perform(p -> p.sendMessage(AussieBedwars.PREFIX + "§c" + text));
    }

    private String generateTimeText() {
        final StringBuilder strB = new StringBuilder("§eStarting in §7§l[§r");

        byte start = 0x00;
        final long toColor = 0x14 * (bedwarsGame.getStartTimer() - missingtime) / bedwarsGame.getStartTimer();
        while (start <= 0x14) {
            strB.append('§').append(start > toColor ? 'c' : 'a').append('|');
            start++;
        }
        return strB.append("§7§l] §6" + missingtime + " §eseconds").toString();
    }

    private String generateMissingPlayerText() {
        return "§7[§eAussieBedwars§7]: Missing §e" + (bedwarsGame.getMinPlayers() - associatedQueue.queueSize()) + " §7more players to play";
    }

    public void tickTimer() {
        timerTask = plugin.getServer()
                .getScheduler()
                .runTaskTimer(plugin, () -> {
                    if (hasStarted) {
                        timerTask.cancel();
                        return;
                    }
                    if (associatedQueue.isEmpty()) {
                        missingtime = bedwarsGame.getStartTimer();
                        return;
                    }

                    if (associatedQueue.queueSize() >= bedwarsGame.getMinGames()) {
                        final GameStartEvent event = new GameStartEvent(this);
                        plugin.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) {
                            hasStarted = true;
                            start();
                        }
                        return;
                    }

                    associatedQueue.perform(p -> {
                        final PlayerConnection conn = GameUtils.getPlayerConnection(p);
                        final IChatBaseComponent iChat;
                        final boolean tickTime = associatedQueue.queueSize() >= bedwarsGame.getMinPlayers();
                        if (tickTime) {
                            iChat = new ChatMessage(generateTimeText());
                            missingtime--;
                        } else {
                            iChat = new ChatMessage(generateMissingPlayerText());
                        }
                        final PacketPlayOutChat chatPacket = new PacketPlayOutChat(iChat, (byte) 0x2);
                        conn.sendPacket(chatPacket);
                    });

                }, 1L, 20L);
    }

    public abstract void start();

    public abstract void moveTeamsToSpawns();

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
                associatedQueue.addToQueue(player);
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

    public boolean isHasStarted() {
        return hasStarted;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }
}
