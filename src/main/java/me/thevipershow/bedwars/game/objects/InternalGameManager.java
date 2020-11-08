package me.thevipershow.bedwars.game.objects;

import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import me.thevipershow.bedwars.game.AbstractDeathmatch;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ExperienceManager;
import me.thevipershow.bedwars.game.GameInventories;
import me.thevipershow.bedwars.game.GameLobbyTicker;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.KillTracker;
import me.thevipershow.bedwars.game.QuestManager;
import me.thevipershow.bedwars.listeners.game.GameTrapTriggerer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public final class InternalGameManager {

    public InternalGameManager(AbstractDeathmatch abstractDeathmatch, ExperienceManager experienceManager, QuestManager questManager, GameTrapTriggerer gameTrapTriggerer, KillTracker killTracker, GameInventories gameInventories, BedwarsGame bedwarsGame, TeamManager<?> teamManager, ListenersManager listenersManager, GameLobbyTicker gameLobbyTicker, Plugin plugin, CachedGameData cachedGameData, ActiveSpawnersManager activeSpawnersManager, MovementsManager movementsManager, InvisibilityManager invisibilityManager, PlayerMapper playerMapper, ScoreboardManager scoreboardManager, MerchantManager merchantManager, TrapsManager trapsManager, MapManager mapManager) {
        this.abstractDeathmatch = abstractDeathmatch;
        this.experienceManager = experienceManager;
        this.questManager = questManager;
        this.gameTrapTriggerer = gameTrapTriggerer;
        this.killTracker = killTracker;
        this.gameInventories = gameInventories;
        this.bedwarsGame = bedwarsGame;
        this.teamManager = teamManager;
        this.listenersManager = listenersManager;
        this.gameLobbyTicker = gameLobbyTicker;
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
    private final GameInventories gameInventories;
    // The BedwarsGame object is used to load this game and contains essentials info
    // about the data and the structure of the game and map that the players will play on.
    private final BedwarsGame bedwarsGame;
    // The TeamManager is used to manage teams, by adding players, removing them, or changing
    // their current state during gameplay. The players are added onto it upon start();
    private final TeamManager<?> teamManager;
    // This class is used to register or unregister game listeners.
    private final ListenersManager listenersManager;
    // For lobby
    private final GameLobbyTicker gameLobbyTicker;
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
    //━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    public static InternalGameManager build(ActiveGame activeGame, String gameWorldFilename, BedwarsGame bedwarsGame, World lobbyWorld, Plugin plugin) {
        final ExperienceManager experienceManager = new ExperienceManager(activeGame);
        final Gamemode gamemode = bedwarsGame.getGamemode();
        return new InternalGameManager(GameUtils.deathmatchFromGamemode(gamemode, activeGame),
                experienceManager,
                new QuestManager(experienceManager),
                new GameTrapTriggerer(activeGame),
                new KillTracker(activeGame),
                new GameInventories(activeGame),
                bedwarsGame,
                gamemode == Gamemode.SOLO ? new SoloTeamManager(activeGame) : new MultiTeamManager(activeGame),
                new ListenersManager(activeGame),
                new GameLobbyTicker(activeGame),
                plugin,
                new CachedGameData(gameWorldFilename, lobbyWorld, bedwarsGame),
                new ActiveSpawnersManager(activeGame),
                new MovementsManager(activeGame),
                new InvisibilityManager(activeGame),
                new PlayerMapper(),
                new ScoreboardManager(activeGame),
                new MerchantManager(activeGame),
                new TrapsManager(activeGame),
                new MapManager(activeGame));
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

    public final GameInventories getGameInventories() {
        return gameInventories;
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

    public final GameLobbyTicker getGameLobbyTicker() {
        return gameLobbyTicker;
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
