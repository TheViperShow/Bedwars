package me.thevipershow.bedwars.game.objects;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
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

@Builder(access = AccessLevel.PRIVATE)
@Getter
public final class InternalGameManager {

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
    private final TeamManager teamManager;
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
    //━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    public static InternalGameManager build(ActiveGame activeGame, String gameWorldFilename, BedwarsGame bedwarsGame, World lobbyWorld, Plugin plugin) {
        ExperienceManager experienceManager = new ExperienceManager(activeGame);
        return builder()
                .abstractDeathmatch(GameUtils.deathmatchFromGamemode(bedwarsGame.getGamemode(), activeGame))
                .experienceManager(experienceManager)
                .questManager(new QuestManager(experienceManager))
                .gameTrapTriggerer(new GameTrapTriggerer(activeGame))
                .killTracker(new KillTracker(activeGame))
                .gameInventories(new GameInventories(bedwarsGame.getShop()))
                .bedwarsGame(bedwarsGame)
                .teamManager(bedwarsGame.getGamemode() == Gamemode.SOLO ? new SoloTeamManager(activeGame) : new MultipleTeamManager(activeGame))
                .listenersManager(new ListenersManager(activeGame))
                .gameLobbyTicker(new GameLobbyTicker(activeGame))
                .plugin(plugin)
                .cachedGameData(new CachedGameData(gameWorldFilename, lobbyWorld, bedwarsGame))
                .activeSpawnersManager(new ActiveSpawnersManager(activeGame))
                .movementsManager(new MovementsManager(activeGame))
                .invisibilityManager(new InvisibilityManager(activeGame))
                .build();
    }
}
