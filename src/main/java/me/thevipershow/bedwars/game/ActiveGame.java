package me.thevipershow.bedwars.game;

import java.util.Objects;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import me.thevipershow.bedwars.api.ActiveGameEvent;
import me.thevipershow.bedwars.api.ActiveGameTerminateEvent;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.deathmatch.AbstractDeathmatch;
import me.thevipershow.bedwars.game.managers.QuestManager;
import me.thevipershow.bedwars.game.managers.ToolsAndArmorManager;
import me.thevipershow.bedwars.game.managers.ExperienceManager;
import me.thevipershow.bedwars.game.managers.GameInventoriesManager;
import me.thevipershow.bedwars.game.managers.LobbyManager;
import me.thevipershow.bedwars.game.managers.ActiveSpawnersManager;
import me.thevipershow.bedwars.game.managers.BedManager;
import me.thevipershow.bedwars.game.data.game.CachedGameData;
import me.thevipershow.bedwars.listeners.GameListener;
import me.thevipershow.bedwars.game.managers.InternalGameManager;
import me.thevipershow.bedwars.game.managers.InvisibilityManager;
import me.thevipershow.bedwars.game.managers.ListenersManager;
import me.thevipershow.bedwars.game.managers.MapManager;
import me.thevipershow.bedwars.game.managers.MerchantManager;
import me.thevipershow.bedwars.game.managers.MovementsManager;
import me.thevipershow.bedwars.game.managers.MultiTeamManager;
import me.thevipershow.bedwars.game.data.game.PlayerMapper;
import me.thevipershow.bedwars.game.data.game.enums.PlayerState;
import me.thevipershow.bedwars.game.managers.ScoreboardManager;
import me.thevipershow.bedwars.game.managers.SoloTeamManager;
import me.thevipershow.bedwars.game.managers.TeamManager;
import me.thevipershow.bedwars.game.managers.TrapsManager;
import me.thevipershow.bedwars.game.managers.UpgradesManager;
import me.thevipershow.bedwars.game.runnables.GameTrapTriggerer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

/**
 * This class represents the implementation of a running game.
 * This class is responsible for every single action happening
 * during a 'Bedwars' game.
 * All of the important data is saved inside the {@link InternalGameManager}
 * object, which is loaded at runtime.
 * This class should be deleted as soon as the game is determined to be finished.
 */
@SuppressWarnings({"JavaDoc", "FieldCanBeLocal"})
public final class ActiveGame {

    // the time the game started at:
    private long startTime;
    // This game current state:
    private ActiveGameState gameState = ActiveGameState.NONE;
    // Other fields:
    private final InternalGameManager internalGameManager;
    private final BedwarsGame bedwarsGame;
    private final World lobbyWorld;
    private final World gameWorld;
    private final Plugin plugin;

    /*---------------------------------------------------------------------------------------------------------------*/

    public ActiveGame(World gameWorld, BedwarsGame bedwarsGame, World lobbyWorld, Plugin plugin) {
        this.bedwarsGame = Objects.requireNonNull(bedwarsGame, "BedwarsGame was null during ActiveGame creation.");
        this.plugin = Objects.requireNonNull(plugin, "Plugin was null during ActiveGame creation.");
        this.lobbyWorld = Objects.requireNonNull(lobbyWorld, "lobby World was null during ActiveGame creation.");
        this.gameWorld = Objects.requireNonNull(gameWorld, "game World was null during ActiveGame creation.");
        ExperienceManager experienceManager = new ExperienceManager(this);
        this.internalGameManager = new InternalGameManager(GameUtils.deathmatchFromGamemode(bedwarsGame.getGamemode(), this),
                experienceManager,
                new QuestManager(experienceManager),
                new GameTrapTriggerer(this),
                new KillTracker(this),
                new GameInventoriesManager(this),
                bedwarsGame,
                bedwarsGame.getGamemode() == Gamemode.SOLO ? new SoloTeamManager(this) : new MultiTeamManager(this),
                new ListenersManager(this),
                new LobbyManager(this),
                plugin,
                new CachedGameData(gameWorld, lobbyWorld, bedwarsGame),
                new ActiveSpawnersManager(this),
                new MovementsManager(this),
                new InvisibilityManager(this),
                new PlayerMapper(),
                new ScoreboardManager(this),
                new MerchantManager(this),
                new TrapsManager(this),
                new MapManager(this),
                new BedManager(this),
                new UpgradesManager(this),
                new ToolsAndArmorManager(this));
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * INITIALIZATION PHASE:
     * This method represents the initialization of the ActiveGame.
     * When the ActiveGame enters the initialization stage it is crucial
     * that its gameState is set to {@link ActiveGameState#INITIALIZING}
     * <p>
     * During this phase all listeners that are marked for the registration phase
     * {@link GameListener.RegistrationStage#INITIALIZATION}
     * will be registered via the {@link ListenersManager#enableAllByPhase(GameListener.RegistrationStage)}
     * <p>
     * This phase is also associated with the 'queue' phase.
     * During this phase players will be forming a queue into the area
     * designed for players to wait the start of the game.
     * Nonetheless it is considered essential to start the ticking of the lobby
     * and start the matchmaking algorithms in this stage.
     * <p>
     * Other objects may be instantiated in this stage,
     * such as spawners and\or merchants.
     * <p>
     * As soon as this phase finished the game will move
     * to the {@link ActiveGameState#STARTED} phase using the {@link ActiveGame#start()} method.
     */
    public final void initialize() {
        setGameState(ActiveGameState.INITIALIZING); // setting game state to initializing.

        getListenersManager().enableAllByPhase(GameListener.RegistrationStage.INITIALIZATION); // registering map and movement protection.
        // other listeners are not required before start of game!
        getGameLobbyTicker().startTicking(); // starting to tick,

        setGameState(ActiveGameState.QUEUE);

        getActiveSpawnersManager().addSpawners();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * START PHASE:
     * This method represents the start of the ActiveGame.
     * It is called right after the initialization stage has finished,
     * hence we set the gameState to {@link ActiveGameState#STARTED}.
     * <p>
     * This method should only be called when the minimum
     * amount of player is present in the queue. This value
     * is determined by the method {@link BedwarsGame#getMinPlayers()}.
     * <p>
     * When a game starts it is crucial to enable all of the listeners
     * that have been marked with a registration stage of type
     * {@link GameListener.RegistrationStage#STARTUP},
     * however it is not less important that we don't forget to
     * remove and disable all of the listeners that have been previously
     * started during the initialization phase.
     * <p>
     * Several things are done after the previously listed:
     * we get all of the Players from {@link LobbyManager#getAssociatedQueue()}
     * and we automatically assign teams. This allows us to have an easily
     * obtainable list of objects that are custom players and store data
     * relative to this game instance.
     * When a game starts all of the Players objects will be used
     * to form teams and build all of the {@link BedwarsPlayer} objects.
     * It is important to note that during this phase these objects may be removed from the
     * managers that store them.
     * The only cause that can trigger the removal of a BedwarsPlayer object from this game's managers
     * is the {@link org.bukkit.event.player.PlayerQuitEvent} which will trigger instantaneous removal
     * of all references of that player from this game.
     * <p>
     * What is to be done after this, is assigning all objects that will be necessary for a
     * correct game experience, such as:
     * - game traps,
     * - spawners
     * - merchants
     * - deathmatch mode manager
     * - scoreboards
     * etc.
     * <p>
     * This phase will only come to an end when a single teams is left in the game,
     * and as soon as that happens, the method {@link #start()} should be called.
     */
    public final void start() {
        this.startTime = System.currentTimeMillis();

        setGameState(ActiveGameState.STARTED); // setting start time as now.

        getListenersManager().disable(GameListener.QUEUE); // removing queue listener; not required.
        getListenersManager().enableAllByPhase(GameListener.RegistrationStage.STARTUP);

        getGameLobbyTicker().stopTicking(); // stop ticking.

        getPlayerMapper().addAll(getGameLobbyTicker().getAssociatedQueue().getInQueue()); // Assign mapping for players
        // and BedwarsPlayer objects.
        getTeamManager().assignTeams(); // IMPORTANT:
        // Teams must be assigned after the player mapper has been correctly
        // filled with the players from the AbstractQueue.

        getTeamManager().cleanAllInventories(); // cleaning to be sure.

        getTeamManager().setEveryoneStatus(PlayerState.PLAYING); // setting everyone's status to playing

        getBedManager().destroyInactiveBeds(); // removing all beds that are not assigned from the map

        getGameInventoriesManager().assignPlayerShop(); // assigning a shop to each player

        getGameInventoriesManager().assignUpgradeLevelsToAll(); // setting all upgrade levels to -1 for everyoneò

        getTrapsManager().fillTraps();      // filling traps list, we made sure teams are already assigned
        getTrapsManager().fillTrapsDelay(); // filling the last activation time

        getTeamManager().updateBedwarsPlayersTeam(); // Updating the team field in each of the BedwarsPlayer objects
        // (we should rely on it as less as possible).

        getActiveSpawnersManager().createAnnouncements(); // Creating update announcements.
        getActiveSpawnersManager().spawnAll();            // Spawn all spawners

        getMerchantManager().createAll(); // First creating all AbstractMerchants
        getMerchantManager().spawnAll();  // then spawning them in the game

        getScoreboardManager().assignScoreboards(); // first we assign all,
        getScoreboardManager().activateAll();       // then we activate them.

        getAbstractDeathmatch().start();            // starting the deathmatch

        getMovementsManager().moveToSpawnpoints();  // Ideally we want everything to get generated before teleporting.

        getToolsAndArmorManager().giveDefaultColoredSet(); // giving default colored leather armor with enchant.
        getToolsAndArmorManager().giveDefaultSword(); // give wood sword

        getExperienceManager().startRewardTask(); // start reward task
    }

    /**
     * STOP PHASE:
     * This method stops the game.
     * All references of the game will be removed by listeners
     * of {@link ActiveGameTerminateEvent} to avoid memory leaks.
     */
    public final void stop() {
        setGameState(ActiveGameState.FINISHED);

        getExperienceManager().stopRewardTask(); // stop reward task

        getScoreboardManager().deactivateAllScoreboards(); // deactivating scoreboards

        getActiveSpawnersManager().cancelAnnouncements(); // removing all announcement tasks

        getActiveSpawnersManager().cancelAllSpawners(); // cancel all spawners

        getMovementsManager().moveAllSpawn();

        getListenersManager().disableAllByPhase(GameListener.RegistrationStage.STARTUP);
        getListenersManager().disableAllByPhase(GameListener.RegistrationStage.INITIALIZATION);

        getTeamManager().cleanAllEffects(); // clean all effects.
        getTeamManager().cleanAllInventories(); // clean all inventories.
        getQuestManager().rewardAllAtEndGame(); // reward all for playing until the end

        // The game is marked as finished from now on.
        callGameEvent(new ActiveGameTerminateEvent(this));
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    public final void callGameEvent(final ActiveGameEvent activeGameEvent) {
        this.plugin.getServer().getPluginManager().callEvent(activeGameEvent);
    }

    public final ToolsAndArmorManager getToolsAndArmorManager() {
        return internalGameManager.getArmorManager();
    }

    public final UpgradesManager getUpgradesManager() {
        return internalGameManager.getUpgradesManager();
    }

    public final BedManager getBedManager() {
        return internalGameManager.getBedDestroyer();
    }

    public final long getStartTime() {
        return startTime;
    }

    public final TrapsManager getTrapsManager() {
        return internalGameManager.getTrapsManager();
    }

    public final MovementsManager getMovementsManager() {
        return internalGameManager.getMovementsManager();
    }

    public final MerchantManager getMerchantManager() {
        return internalGameManager.getMerchantManager();
    }

    public final AbstractDeathmatch getAbstractDeathmatch() {
        return internalGameManager.getAbstractDeathmatch();
    }

    public final void setGameState(ActiveGameState gameState) {
        this.gameState = gameState;
    }

    public final ExperienceManager getExperienceManager() {
        return internalGameManager.getExperienceManager();
    }

    public final QuestManager getQuestManager() {
        return internalGameManager.getQuestManager();
    }

    public final GameTrapTriggerer getGameTrapTriggerer() {
        return internalGameManager.getGameTrapTriggerer();
    }

    public final KillTracker getKillTracker() {
        return internalGameManager.getKillTracker();
    }

    public final GameInventoriesManager getGameInventoriesManager() {
        return internalGameManager.getGameInventories();
    }

    public final BedwarsGame getBedwarsGame() {
        return this.bedwarsGame;
    }

    public final TeamManager<?> getTeamManager() {
        return internalGameManager.getTeamManager();
    }

    public final ListenersManager getListenersManager() {
        return internalGameManager.getListenersManager();
    }

    public final LobbyManager getGameLobbyTicker() {
        return internalGameManager.getGameLobbyTicker();
    }

    public final Plugin getPlugin() {
        return this.plugin;
    }

    public final CachedGameData getCachedGameData() {
        return internalGameManager.getCachedGameData();
    }

    public final ActiveSpawnersManager getActiveSpawnersManager() {
        return internalGameManager.getActiveSpawnersManager();
    }

    public final ActiveGameState getGameState() {
        return gameState;
    }

    public final InternalGameManager getInternalGameManager() {
        return internalGameManager;
    }

    public final ScoreboardManager getScoreboardManager() {
        return internalGameManager.getScoreboardManager();
    }

    public final PlayerMapper getPlayerMapper() {
        return internalGameManager.getPlayerMapper();
    }
}
