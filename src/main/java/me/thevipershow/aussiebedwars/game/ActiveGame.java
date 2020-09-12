package me.thevipershow.aussiebedwars.game;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.config.objects.Merchant;
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
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
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
    protected final Set<Player> playersOutOfGame = new HashSet<>();
    protected final Set<AbstractActiveMerchant> activeMerchants = new HashSet<>();
    protected final Set<Block> playerPlacedBlocks = new HashSet<>();
    protected final Set<UnregisterableListener> unregisterableListeners = new HashSet<>();

    ///////////////////////////////////////////////////
    // Internal ActiveGame fields                    //
    //-----------------------------------------------//
    //                                               //
    protected boolean hasStarted = false;            //
    protected boolean winnerDeclared = false;
    protected BukkitTask timerTask = null;           //
    protected final GameLobbyTicker gameLobbyTicker;
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
        this.assignedTeams = new HashMap<>();
        this.gameLobbyTicker = new GameLobbyTicker(this);
        this.activeSpawners = bedwarsGame.getSpawners()
                .stream()
                .map(spawner -> new ActiveSpawner(spawner, this))
                .collect(Collectors.toSet());


        registerMapListeners();
        gameLobbyTicker.startTicking();
    }

    public void handleError(String text) {
        associatedQueue.perform(p -> p.sendMessage(AussieBedwars.PREFIX + "§c" + text));
    }

    public void moveToLobby(Player player) {
        player.teleport(cachedLobbySpawnLocation);
    }

    public void moveAllToLobby() {
        associatedWorld.getPlayers().forEach(p -> {
            p.teleport(getCachedLobbySpawnLocation());
        });
    }

    public static void connectedToQueue(final Player player, final ActiveGame activeGame) {
        player.sendMessage(AussieBedwars.PREFIX + "§eYou have joined §7" + activeGame.getAssociatedWorld().getName() + " §equeue");
        player.sendMessage(AussieBedwars.PREFIX + String.format("§eStatus §7[§a%d§8/§a%d§7]", activeGame.getAssociatedQueue().queueSize() + 1, activeGame.getBedwarsGame().getPlayers()));
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
                player.setGameMode(GameMode.ADVENTURE);
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

    public BedwarsTeam findWinningTeam() {
        if (teamsLeft().size() == 1) {
            return teamsLeft().stream().findAny().get();
        } else {
            return null;
        }
    }

    public Set<Player> getTeamPlayers(final BedwarsTeam bedwarsTeam) {
        for (final Map.Entry<BedwarsTeam, Set<Player>> entry : assignedTeams.entrySet()) {
            if (entry.getKey() == bedwarsTeam)
                return entry.getValue();
        }
        return null;
    }

    public Set<BedwarsTeam> teamsLeft() {
        if (playersOutOfGame.isEmpty()) return Collections.emptySet();
        final Set<BedwarsTeam> bedwarsTeams = new HashSet<>();
        for (final Map.Entry<BedwarsTeam, Set<Player>> entry : assignedTeams.entrySet()) {
            final BedwarsTeam team = entry.getKey();
            final Set<Player> playerSet = entry.getValue();
            final boolean isTeamOutOfGame = playersOutOfGame.containsAll(playerSet);
            if (!isTeamOutOfGame) {
                bedwarsTeams.add(team);
            }
        }
        return bedwarsTeams;
    }

    public abstract void declareWinner(final BedwarsTeam player);

    public abstract void assignTeams();

    public abstract void assignScoreboards();

    public abstract void destroyTeamBed(final BedwarsTeam team);

    public void createSpawners() {
        for (final ActiveSpawner activeSpawner : getActiveSpawners())
            activeSpawner.spawn();
    }

    public void destroyMap() {
        final boolean unloaded = plugin.getServer().unloadWorld(associatedWorld, false);
        plugin.getLogger().info((unloaded ? "Successfully" : "Failed") + " Unloaded game " + associatedWorldFilename);
        final File wDir = associatedWorld.getWorldFolder();
        try {
            FileUtils.deleteDirectory(wDir);
        } catch (IOException e) {
            plugin.getLogger().severe("Something went wrong while destroying map of " + associatedWorldFilename);
            e.printStackTrace();
        }
    }

    public void createMerchants() {
        for (final Merchant merchant : bedwarsGame.getMerchants()) {
            final AbstractActiveMerchant aMerchant = GameUtils.fromMerchant(merchant, this);
            if (aMerchant == null) {
                continue;
            }
            aMerchant.spawn();
            this.activeMerchants.add(aMerchant);
        }
    }

    protected String getTeamChar(final BedwarsTeam t) {
        if (assignedTeams.get(t) == null || assignedTeams.get(t).isEmpty()) {
            return " §c§l✘";
        } else {
            return " §a§l✓";
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

    public Set<Block> getPlayerPlacedBlocks() {
        return playerPlacedBlocks;
    }

    public World getLobbyWorld() {
        return lobbyWorld;
    }

    public Set<Player> getPlayersOutOfGame() {
        return playersOutOfGame;
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

    public Set<ActiveSpawner> getActiveSpawners() {
        return activeSpawners;
    }

    public Set<AbstractActiveMerchant> getActiveMerchants() {
        return activeMerchants;
    }

    @Override
    public String toString() {
        return "ActiveGame{" +
                "associatedWorldFilename='" + associatedWorldFilename + '\'' +
                ", bedwarsGame=" + bedwarsGame +
                ", associatedWorld=" + associatedWorld +
                ", lobbyWorld=" + lobbyWorld +
                ", cachedLobbySpawnLocation=" + cachedLobbySpawnLocation +
                ", plugin=" + plugin +
                ", associatedQueue=" + associatedQueue +
                ", cachedWaitingLocation=" + cachedWaitingLocation +
                ", assignedTeams=" + assignedTeams +
                ", activeSpawners=" + activeSpawners +
                ", activeScoreboards=" + activeScoreboards +
                ", destroyedTeams=" + destroyedTeams +
                ", activeMerchants=" + activeMerchants +
                ", unregisterableListeners=" + unregisterableListeners +
                ", hasStarted=" + hasStarted +
                ", timerTask=" + timerTask +
                '}';
    }

    public Set<BedwarsTeam> getDestroyedTeams() {
        return destroyedTeams;
    }

    public boolean isWinnerDeclared() {
        return winnerDeclared;
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

    public BukkitTask getTimerTask() {
        return timerTask;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }
}
