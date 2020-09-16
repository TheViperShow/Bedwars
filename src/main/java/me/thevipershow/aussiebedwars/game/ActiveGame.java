package me.thevipershow.aussiebedwars.game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.config.objects.Merchant;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import me.thevipershow.aussiebedwars.listeners.game.ArmorSet;
import me.thevipershow.aussiebedwars.listeners.game.BedBreakListener;
import me.thevipershow.aussiebedwars.listeners.game.DeathListener;
import me.thevipershow.aussiebedwars.listeners.game.EntityDamageListener;
import me.thevipershow.aussiebedwars.listeners.game.ExplosionListener;
import me.thevipershow.aussiebedwars.listeners.game.GUIInteractListener;
import me.thevipershow.aussiebedwars.listeners.game.HungerLossListener;
import me.thevipershow.aussiebedwars.listeners.game.LobbyCompassListener;
import me.thevipershow.aussiebedwars.listeners.game.MapIllegalMovementsListener;
import me.thevipershow.aussiebedwars.listeners.game.MapProtectionListener;
import me.thevipershow.aussiebedwars.listeners.game.MerchantInteractListener;
import me.thevipershow.aussiebedwars.listeners.game.PlayerFireballInteractListener;
import me.thevipershow.aussiebedwars.listeners.game.PlayerQuitDuringGameListener;
import me.thevipershow.aussiebedwars.listeners.game.SpectatorsInteractListener;
import me.thevipershow.aussiebedwars.listeners.game.TNTPlaceListener;
import me.thevipershow.aussiebedwars.worlds.WorldsManager;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
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
    protected final Map<BedwarsTeam, List<Player>> assignedTeams;
    protected final Set<ActiveSpawner> activeSpawners;

    protected final List<Scoreboard> activeScoreboards = new ArrayList<>();
    protected final List<BedwarsTeam> destroyedTeams = new ArrayList<>();
    protected final List<Player> playersOutOfGame = new ArrayList<>();
    protected final List<AbstractActiveMerchant> activeMerchants = new ArrayList<>();
    protected final List<Block> playerPlacedBlocks = new ArrayList<>();
    protected final List<UnregisterableListener> unregisterableListeners = new ArrayList<>();
    protected final Map<Player, ArmorSet> playerSetMap = new HashMap<>();
    protected final Map<String, Integer> topKills = new HashMap<>();

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

    protected final ScoreboardHandler scoreboardHandler = new ScoreboardHandler() {

        @Override
        public String getTitle(final Player player) {
            return AussieBedwars.PREFIX;
        }

        @Override
        public List<Entry> getEntries(final Player player) {
            final EntryBuilder builder = new EntryBuilder();
            builder.blank();
            for (BedwarsTeam t : assignedTeams.keySet()) {
                builder.next(" §7Team " + "§l§" + t.getColorCode() + t.name() + getTeamChar(t));
            }
            builder.blank();
            return builder.build();
        }

    };

    public void destroyTeamBed(final BedwarsTeam team) {
        associatedQueue.perform(p -> {
            if (p.isOnline() && p.getWorld().equals(associatedWorld)) {
                if (getPlayerTeam(p) == team) {
                    p.sendTitle("§e§lYour bed has been broken!", "");
                    p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 10.0f, 1.0f);
                } else {
                    p.sendMessage("§" + team.getColorCode() + team.name() + " §7team's bed has been broken!");
                }
            }
        });
    }

    public final void cleanAllAndFinalize() {
        unregisterAllListeners();
        associatedQueue.cleanQueue();
        assignedTeams.clear();
        activeSpawners.forEach(ActiveSpawner::despawn);
        activeSpawners.clear();
        activeScoreboards.forEach(Scoreboard::deactivate);
        activeScoreboards.clear();
        destroyedTeams.clear();
        playersOutOfGame.clear();
        activeMerchants.forEach(AbstractActiveMerchant::delete);
        activeMerchants.clear();
        playerPlacedBlocks.clear();
        playerSetMap.clear();
        topKills.clear();
        gameLobbyTicker.stopTicking();
        hasStarted = false;
        destroyMap();
        try {
            finalize();
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    public void handleError(String text) {
        associatedQueue.perform(p -> p.sendMessage(AussieBedwars.PREFIX + "§c" + text));
    }

    public void moveToLobby(Player player) {
        player.teleport(cachedLobbySpawnLocation);
    }

    public void moveAllToLobby() {
        associatedWorld.getPlayers().forEach(p -> {
            GameUtils.clearAllEffects(p);
            p.setGameMode(GameMode.SURVIVAL);
            p.setFallDistance(0.100f);
            GameUtils.clearArmor(p);
            p.getInventory().clear();
            p.teleport(getCachedLobbySpawnLocation());
            p.setAllowFlight(false);
            p.setFlying(false);
        });
    }

    public static void connectedToQueue(final Player player, final ActiveGame activeGame) {
        activeGame.getAssociatedWorld().getPlayers().forEach(p -> {
            player.sendMessage(AussieBedwars.PREFIX + p.getName() + " §ehas joined §7" + activeGame.getAssociatedWorld().getName() + " §equeue");
            player.sendMessage(AussieBedwars.PREFIX + String.format("§eStatus §7[§a%d§8/§a%d§7]", activeGame.getAssociatedQueue().queueSize() + 1, activeGame.getBedwarsGame().getPlayers()));
        });
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
        final UnregisterableListener hungerLossListener = new HungerLossListener(this);
        final UnregisterableListener spectatorInteractListener = new SpectatorsInteractListener(this);
        final UnregisterableListener tntPlaceListener = new TNTPlaceListener(this);
        final UnregisterableListener fireballInteract = new PlayerFireballInteractListener(this);
        final UnregisterableListener explosionListener = new ExplosionListener(this);

        plugin.getServer().getPluginManager().registerEvents(mapProtectionListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(mapIllegalMovementsListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(bedDestroyListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(lobbyCompassListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(deathListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(quitListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(merchantListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(entityDamageListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(guiInteractListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(hungerLossListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(spectatorInteractListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(tntPlaceListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(fireballInteract, plugin);
        plugin.getServer().getPluginManager().registerEvents(explosionListener, plugin);

        unregisterableListeners.add(mapIllegalMovementsListener);
        unregisterableListeners.add(mapProtectionListener);
        unregisterableListeners.add(bedDestroyListener);
        unregisterableListeners.add(lobbyCompassListener);
        unregisterableListeners.add(deathListener);
        unregisterableListeners.add(quitListener);
        unregisterableListeners.add(merchantListener);
        unregisterableListeners.add(entityDamageListener);
        unregisterableListeners.add(guiInteractListener);
        unregisterableListeners.add(hungerLossListener);
        unregisterableListeners.add(spectatorInteractListener);
        unregisterableListeners.add(tntPlaceListener);
        unregisterableListeners.add(fireballInteract);
        unregisterableListeners.add(explosionListener);
    }

    public final void unregisterAllListeners() {
        for (final UnregisterableListener unregisterableListener : unregisterableListeners) {
            if (!unregisterableListener.isUnregistered()) {
                unregisterableListener.unregister();
            }
        }
    }

    public void moveToWaitingRoom(final Player player) {
        if (cachedWaitingLocation != null) {
            if (player.teleport(cachedWaitingLocation)) {
                connectedToQueue(player, this);
                player.setGameMode(GameMode.ADVENTURE);
            }
        } else
            player.sendMessage(AussieBedwars.PREFIX + "Something went wrong when teleporting you to waiting room.");
    }

    public BedwarsTeam getPlayerTeam(final Player player) {
        for (final Map.Entry<BedwarsTeam, List<Player>> entry : assignedTeams.entrySet())
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

    public void start() {
        setHasStarted(true);
        if (associatedQueue.queueSize() >= bedwarsGame.getMinPlayers()) {
            if (associatedWorld == null) {
                handleError("Something went wrong while you were being sent into game.");
                return;
            }
            assignTeams();
            assignScoreboards();
            createSpawners();
            createMerchants();
            moveTeamsToSpawns();
            giveAllDefaultSet();
            healAll();
        }
    }

    public void healAll() {
        associatedWorld.getPlayers().forEach(p -> {
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(0x14);
        });
    }

    public abstract void moveTeamsToSpawns();

    public void givePlayerDefaultSet(final Player p) {
        final ArmorSet startingSet = new ArmorSet(getPlayerTeam(p));
        startingSet.getArmorSet().forEach((k, v) -> ArmorSet.Slots.setArmorPiece(k, p, startingSet.getArmorSet().get(k)));
        GameUtils.giveStackToPlayer(new ItemStack(Material.WOOD_SWORD, 1), p, p.getInventory().getContents());
        this.playerSetMap.put(p, startingSet);
    }

    public void giveAllDefaultSet() {
        associatedQueue.perform(this::givePlayerDefaultSet);
    }

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

    public List<Player> getTeamPlayers(final BedwarsTeam bedwarsTeam) {
        for (final Map.Entry<BedwarsTeam, List<Player>> entry : assignedTeams.entrySet()) {
            if (entry.getKey() == bedwarsTeam)
                return entry.getValue();
        }
        return null;
    }

    public Set<BedwarsTeam> teamsLeft() {
        if (playersOutOfGame.isEmpty()) return Collections.emptySet();
        final Set<BedwarsTeam> bedwarsTeams = new HashSet<>();
        for (final Map.Entry<BedwarsTeam, List<Player>> entry : assignedTeams.entrySet()) {
            final BedwarsTeam team = entry.getKey();
            final List<Player> playerSet = entry.getValue();
            final boolean isTeamOutOfGame = playersOutOfGame.containsAll(playerSet);
            if (!isTeamOutOfGame) {
                bedwarsTeams.add(team);
            }
        }
        return bedwarsTeams;
    }

    public void declareWinner(final BedwarsTeam team) {
        if (winnerDeclared) return;
        associatedQueue.perform(p -> {
            if (p.isOnline() && p.getWorld().equals(associatedWorld)) {
                p.sendTitle("§7Team " + '§' + team.getColorCode() + team.name() + " §7has won the game!", "§7Returning to lobby in 15s");
            }
        });
        this.winnerDeclared = true;
    }

    public void stop() {
        moveAllToLobby();
        cleanAllAndFinalize();
    }

    public abstract void assignTeams();

    public abstract void assignScoreboards();

    public void createSpawners() {
        for (final ActiveSpawner activeSpawner : getActiveSpawners())
            activeSpawner.spawn();
    }

    public void upgradePlayerArmorSet(final Player player, final String type) {
        final ArmorSet pSet = playerSetMap.get(player);
        pSet.upgradeAll(type);
        pSet.getArmorSet().forEach((k, v) -> ArmorSet.Slots.setArmorPiece(k, player, pSet.getArmorSet().get(k)));
    }

    public void downgradePlayerArmorSet(final Player player) {
        // NO
    }

    public boolean isOutOfGame(final Player p) {
        if (p != null) {
            return playersOutOfGame.contains(p);
        }
        return true;
    }

    public void destroyMap() {
        final boolean unloaded = plugin.getServer().unloadWorld(associatedWorld, false);
        plugin.getLogger().info((unloaded ? "Successfully" : "Failed") + " Unloaded game " + associatedWorldFilename);
        final File wDir = associatedWorld.getWorldFolder();
        try {
            FileUtils.deleteDirectory(wDir);
            WorldsManager.getInstanceUnsafe().getActiveGameList().remove(this);
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
        if (destroyedTeams.contains(t)) {
            final List<Player> teamMembers = getTeamPlayers(t);
            if (teamMembers.stream().allMatch(this::isOutOfGame)) {
                return " §c§l✘";
            } else {
                return " §f§l" + teamMembers.size();
            }
        } else {
            return " §a§l✓";
        }
    }

    public List<UnregisterableListener> getUnregisterableListeners() {
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

    public List<Block> getPlayerPlacedBlocks() {
        return playerPlacedBlocks;
    }

    public World getLobbyWorld() {
        return lobbyWorld;
    }

    public List<Player> getPlayersOutOfGame() {
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

    public List<AbstractActiveMerchant> getActiveMerchants() {
        return activeMerchants;
    }

    public List<BedwarsTeam> getDestroyedTeams() {
        return destroyedTeams;
    }

    public boolean isWinnerDeclared() {
        return winnerDeclared;
    }

    public boolean isHasStarted() {
        return hasStarted;
    }

    public Map<BedwarsTeam, List<Player>> getAssignedTeams() {
        return assignedTeams;
    }

    public List<Scoreboard> getActiveScoreboards() {
        return activeScoreboards;
    }

    public BukkitTask getTimerTask() {
        return timerTask;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public Map<Player, ArmorSet> getPlayerSetMap() {
        return playerSetMap;
    }

    public GameLobbyTicker getGameLobbyTicker() {
        return gameLobbyTicker;
    }

    public Map<String, Integer> getTopKills() {
        return topKills;
    }
}
