package me.thevipershow.aussiebedwars.game;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.config.objects.Merchant;
import me.thevipershow.aussiebedwars.events.GameStartEvent;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import me.thevipershow.aussiebedwars.listeners.game.BedBreakListener;
import me.thevipershow.aussiebedwars.listeners.game.DeathListener;
import me.thevipershow.aussiebedwars.listeners.game.EntityDamageListener;
import me.thevipershow.aussiebedwars.listeners.game.GUIInteractListener;
import me.thevipershow.aussiebedwars.listeners.game.LobbyCompassListener;
import me.thevipershow.aussiebedwars.listeners.game.MapIllegalMovementsListener;
import me.thevipershow.aussiebedwars.listeners.game.MapProtectionListener;
import me.thevipershow.aussiebedwars.listeners.game.MerchantInteractListener;
import me.thevipershow.aussiebedwars.listeners.game.PlayerQuitDuringGameListener;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class ActiveGame {

    protected final String associatedWorldFilename;
    protected final BedwarsGame bedwarsGame;
    protected final World associatedWorld;
    protected final World lobbyWorld;
    protected final Location cachedLobbySpawnLocation;
    protected final Plugin plugin;
    protected final AbstractQueue<Player> associatedQueue;
    protected final Location cachedWaitingLocation;
    protected final Map<BedwarsTeam, Set<Player>> assignedTeams;
    protected final Set<ActiveSpawner> activeSpawners;

    protected final Set<Scoreboard> activeScoreboards = new HashSet<>();
    protected final Set<BedwarsTeam> destroyedTeams = new HashSet<>();
    protected final Set<AbstractActiveMerchant> activeMerchants = new HashSet<>();
    protected final Set<UnregisterableListener> unregisterableListeners = new HashSet<>();

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
        this.cachedLobbySpawnLocation = this.lobbyWorld.getSpawnLocation();
        this.associatedQueue = new MatchmakingQueue(bedwarsGame.getPlayers());
        this.cachedWaitingLocation = bedwarsGame.getLobbySpawn().toLocation(associatedWorld);
        this.missingtime = bedwarsGame.getStartTimer();
        this.assignedTeams = new HashMap<>();

        this.activeSpawners = bedwarsGame.getSpawners()
                .stream()
                .map(spawner -> new ActiveSpawner(spawner, this))
                .collect(Collectors.toSet());

        tickTimer();
        registerMapListeners();
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

    protected String getTeamChar(final BedwarsTeam t) {
        if (assignedTeams.get(t) == null || assignedTeams.get(t).isEmpty()) {
            return " §c✘";
        } else {
            return " §a✓";
        }
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

    public void moveToLobby(Player player) {
        player.teleport(cachedLobbySpawnLocation);
    }

    public void moveAllToLobby() {
        associatedQueue.performAndClean(player -> {
            if (player.isOnline() && player.getWorld().equals(associatedWorld))
                player.teleport(cachedLobbySpawnLocation);
        });
    }

    public static void connectedToQueue(final Player player, final ActiveGame activeGame) {
        player.sendMessage(AussieBedwars.PREFIX + "§eYou have joined §7" + activeGame.getAssociatedWorld().getName() + " §equeue");
        player.sendMessage(AussieBedwars.PREFIX + String.format("§eStatus §7[§a%d§8/§a%d§7]", activeGame.getAssociatedQueue().queueSize(), activeGame.getBedwarsGame().getPlayers()));
    }

    public void registerMapListeners() {
        final UnregisterableListener mapProtectionListener = new MapProtectionListener(this);
        final UnregisterableListener mapIllegalMovementsListener = new MapIllegalMovementsListener(this);
        final UnregisterableListener bedDestroyListener = new BedBreakListener(this);
        final UnregisterableListener lobbyCompassListener = new LobbyCompassListener(this);
        final UnregisterableListener deathListener = new DeathListener(this);
        final UnregisterableListener quitListener = new PlayerQuitDuringGameListener(this);
        final UnregisterableListener merchantListener = new MerchantInteractListener(this);
        final UnregisterableListener entityDamageListener = new EntityDamageListener(this);
        final UnregisterableListener guiInteractListener = new GUIInteractListener(this);

        plugin.getServer().getPluginManager().registerEvents(mapProtectionListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(mapIllegalMovementsListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(bedDestroyListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(lobbyCompassListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(deathListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(quitListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(merchantListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(entityDamageListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(guiInteractListener, plugin);

        unregisterableListeners.add(mapIllegalMovementsListener);
        unregisterableListeners.add(mapProtectionListener);
        unregisterableListeners.add(bedDestroyListener);
        unregisterableListeners.add(lobbyCompassListener);
        unregisterableListeners.add(deathListener);
        unregisterableListeners.add(quitListener);
        unregisterableListeners.add(merchantListener);
        unregisterableListeners.add(entityDamageListener);
        unregisterableListeners.add(guiInteractListener);
    }

    public final void unregisterAllListeners() {
        for (final UnregisterableListener unregisterableListener : unregisterableListeners)
            if (!unregisterableListener.isUnregistered())
                unregisterableListener.unregister();
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

    public BedwarsTeam getPlayerTeam(final Player player) {
        for (final Map.Entry<BedwarsTeam, Set<Player>> entry : assignedTeams.entrySet())
            if (entry.getValue().contains(player))
                return entry.getKey();
        return null;
    }

    public boolean isMerchantVillager(final Villager villager) {
        for (AbstractActiveMerchant activeMerchant : getActiveMerchants())
            if (activeMerchant.getVillager() == villager)
                return true;
        return false;
    }

    public abstract void start();

    public abstract void moveTeamsToSpawns();

    public abstract void stop();

    public void removePlayer(final Player p) {
        associatedQueue.removeFromQueue(p);
    }

    public abstract void declareWinner(final Player player);

    public abstract void assignTeams();

    public abstract void assignScoreboards();

    public abstract void destroyTeamBed(final BedwarsTeam team);

    public void createSpawners() {
        for (final ActiveSpawner activeSpawner : getActiveSpawners())
            activeSpawner.spawn();
    }

    public void destroyMap() {
        if (isRunning)
            stop();
        plugin.getServer().unloadWorld(associatedWorld, false);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final File wDir = associatedWorld.getWorldFolder();
            try {
                FileUtils.deleteDirectory(wDir);
            } catch (IOException e) {
                plugin.getLogger().severe("Something went wrong while destroying map of " + associatedWorldFilename);
                e.printStackTrace();
            }
        });
    }

    public void createMerchants() {
        for (final Merchant merchant : bedwarsGame.getMerchants()) {
            final AbstractActiveMerchant aMerchant = GameUtils.fromMerchant(merchant, this);
            if (aMerchant == null) continue;
            aMerchant.spawn();
            this.activeMerchants.add(aMerchant);
        }
    }

    public Set<UnregisterableListener> getUnregisterableListeners() {
        return unregisterableListeners;
    }

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

    public Location getCachedLobbySpawnLocation() {
        return cachedLobbySpawnLocation;
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

    public Set<ActiveSpawner> getActiveSpawners() {
        return activeSpawners;
    }

    public Set<AbstractActiveMerchant> getActiveMerchants() {
        return activeMerchants;
    }

    @Override
    public final String toString() {
        return "ActiveGame{" +
                "associatedWorldFilename='" + associatedWorldFilename + '\'' +
                ", bedwarsGame=" + bedwarsGame +
                ", associatedWorld=" + associatedWorld +
                ", lobbyWorld=" + lobbyWorld +
                ", cachedSpawnLocation=" + cachedLobbySpawnLocation +
                ", plugin=" + plugin +
                ", associatedQueue=" + associatedQueue +
                ", cachedWaitingLocation=" + cachedWaitingLocation +
                ", isRunning=" + isRunning +
                '}';
    }

    public Set<BedwarsTeam> getDestroyedTeams() {
        return destroyedTeams;
    }

    public boolean isHasStarted() {
        return hasStarted;
    }

    public Map<BedwarsTeam, Set<Player>> getAssignedTeams() {
        return assignedTeams;
    }

    public Set<Scoreboard> getActiveScoreboards() {
        return activeScoreboards;
    }

    public long getMissingtime() {
        return missingtime;
    }

    public BukkitTask getTimerTask() {
        return timerTask;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }
}
