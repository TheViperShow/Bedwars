package me.thevipershow.bedwars.game.managers;

import me.thevipershow.bedwars.config.objects.BedwarsGame;
import me.thevipershow.bedwars.game.deathmatch.AbstractDeathmatch;
import me.thevipershow.bedwars.game.KillTracker;
import me.thevipershow.bedwars.game.data.game.CachedGameData;
import me.thevipershow.bedwars.game.data.game.PlayerMapper;
import me.thevipershow.bedwars.game.runnables.GameTrapTriggerer;
import org.bukkit.plugin.Plugin;

public final class InternalGameManager {

    public InternalGameManager(AbstractDeathmatch abstractDeathmatch, ExperienceManager experienceManager, QuestManager questManager, GameTrapTriggerer gameTrapTriggerer, KillTracker killTracker, GameInventoriesManager gameInventoriesManager, BedwarsGame bedwarsGame, TeamManager<?> teamManager, ListenersManager listenersManager, LobbyManager lobbyManager, Plugin plugin, CachedGameData cachedGameData, ActiveSpawnersManager activeSpawnersManager, MovementsManager movementsManager, InvisibilityManager invisibilityManager, PlayerMapper playerMapper, ScoreboardManager scoreboardManager, MerchantManager merchantManager, TrapsManager trapsManager, MapManager mapManager, BedManager bedManager, UpgradesManager upgradesManager, ToolsAndArmorManager toolsAndArmorManager) {
        this.abstractDeathmatch = abstractDeathmatch;
        this.experienceManager = experienceManager;
        this.questManager = questManager;
        this.gameTrapTriggerer = gameTrapTriggerer;
        this.killTracker = killTracker;
        this.gameInventoriesManager = gameInventoriesManager;
        this.bedwarsGame = bedwarsGame;
        this.teamManager = teamManager;
        this.listenersManager = listenersManager;
        this.lobbyManager = lobbyManager;
        this.plugin = plugin;
        this.cachedGameData = cachedGameData;
        this.activeSpawnersManager = activeSpawnersManager;
        this.movementsManager = movementsManager;
        this.invisibilityManager = invisibilityManager;
        this.playerMapper = playerMapper;
        this.scoreboardManager = scoreboardManager;
        this.merchantManager = merchantManager;
        this.trapsManager = trapsManager;
        this.mapManager = mapManager;
        this.bedManager = bedManager;
        this.upgradesManager = upgradesManager;
        this.toolsAndArmorManager = toolsAndArmorManager;
    }

    //━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // AbstractDeathmatch is used to start the deathmatch mode
    // and handle all functions regarding that specific section of the game.
    private final AbstractDeathmatch abstractDeathmatch;
    // Experience manager is used to manage player experience during game.
    // It provides several useful methods to reward or check someone's exp.
    private final ExperienceManager experienceManager;
    // QuestManager is used to check for quest completion during a game.
    // its use goes together with ExperienceManager as completing quests
    // rewards the player with an amount of experience points.
    private final QuestManager questManager;
    // GameTrapTriggerer is used to manage traps via a repeating runnable,
    // it uses the game's data to interact with the game when necessary.
    private final GameTrapTriggerer gameTrapTriggerer;
    // KillTrack is used to manage and store kills and final kills obtained
    // by players during the game. They are displayed at the end of the game via chat message.
    private final KillTracker killTracker;
    // GameInventories is used to load standard inventories at the start of a game
    // and modify\interact with players individual inventories during it.
    private final GameInventoriesManager gameInventoriesManager;
    // The BedwarsGame object is used to load this game and contains essentials info
    // about the data and the structure of the game and map that the players will play on.
    private final BedwarsGame bedwarsGame;
    // The TeamManager is used to manage teams, by adding players, removing them, or changing
    // their current state during gameplay. The players are added onto it upon start();
    private final TeamManager<?> teamManager;
    // This class is used to register or unregister game listeners.
    private final ListenersManager listenersManager;
    // For lobby
    private final LobbyManager lobbyManager;
    // This plugin's instance
    private final Plugin plugin;
    // Cached data for game
    private final CachedGameData cachedGameData;
    // manage the map spawners
    private final ActiveSpawnersManager activeSpawnersManager;
    private final MovementsManager movementsManager;
    private final InvisibilityManager invisibilityManager;
    private final PlayerMapper playerMapper;
    private final ScoreboardManager scoreboardManager;
    private final MerchantManager merchantManager;
    private final TrapsManager trapsManager;
    private final MapManager mapManager;
    private final BedManager bedManager;
    private final UpgradesManager upgradesManager;
    private final ToolsAndArmorManager toolsAndArmorManager;
    //━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━


    public final ToolsAndArmorManager getArmorManager() {
        return toolsAndArmorManager;
    }

    public final UpgradesManager getUpgradesManager() {
        return upgradesManager;
    }

    public final BedManager getBedDestroyer() {
        return bedManager;
    }

    public final MapManager getMapManager() {
        return mapManager;
    }

    public final TrapsManager getTrapsManager() {
        return trapsManager;
    }

    public final ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public final MerchantManager getMerchantManager() {
        return merchantManager;
    }

    public final AbstractDeathmatch getAbstractDeathmatch() {
        return abstractDeathmatch;
    }

    public final ExperienceManager getExperienceManager() {
        return experienceManager;
    }

    public final QuestManager getQuestManager() {
        return questManager;
    }

    public final GameTrapTriggerer getGameTrapTriggerer() {
        return gameTrapTriggerer;
    }

    public final KillTracker getKillTracker() {
        return killTracker;
    }

    public final GameInventoriesManager getGameInventories() {
        return gameInventoriesManager;
    }

    public final BedwarsGame getBedwarsGame() {
        return bedwarsGame;
    }

    public final TeamManager<?> getTeamManager() {
        return teamManager;
    }

    public final ListenersManager getListenersManager() {
        return listenersManager;
    }

    public final LobbyManager getGameLobbyTicker() {
        return lobbyManager;
    }

    public final Plugin getPlugin() {
        return plugin;
    }

    public final CachedGameData getCachedGameData() {
        return cachedGameData;
    }

    public final ActiveSpawnersManager getActiveSpawnersManager() {
        return activeSpawnersManager;
    }

    public final MovementsManager getMovementsManager() {
        return movementsManager;
    }

    public final InvisibilityManager getInvisibilityManager() {
        return invisibilityManager;
    }

    public final PlayerMapper getPlayerMapper() {
        return playerMapper;
    }
}
