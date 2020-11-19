package me.thevipershow.bedwars.game.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.config.folders.BedwarsGameFactory;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import me.thevipershow.bedwars.api.ConnectToQueueEvent;
import me.thevipershow.bedwars.game.AbstractQueue;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveGameState;
import me.thevipershow.bedwars.worlds.WorldsManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class GameManager {

    public GameManager(final JavaPlugin plugin,
                       final WorldsManager worldsManager,
                       final BedwarsGameFactory bedwarsGameFactory) {
        this.plugin = plugin;
        this.worldsManager = worldsManager;
        this.bedwarsGameSet.addAll(bedwarsGameFactory.buildGameObjects());
    }

    private final JavaPlugin plugin;
    private final WorldsManager worldsManager;
    private final List<BedwarsGame> bedwarsGameSet = new ArrayList<>();
    private volatile boolean loading = false;

    public void loadBaseAmount() {
        loading = true;
        final List<Gamemode> allowed = worldsManager.getConfigManager().getDefaultConfiguration().getAttemptLoad();
        for (BedwarsGame bedwarsGame : bedwarsGameSet) {
            if (allowed.contains(bedwarsGame.getGamemode())) {
                for (int i = 0; i < bedwarsGame.getMinGames(); i++) {
                    worldsManager.load(bedwarsGame);
                }
            }
        }
        loading = false;
    }

    public final void loadRandom(final Gamemode gamemode) {
        loading = true;
        for (final BedwarsGame bedwarsGame : bedwarsGameSet) {
            if (bedwarsGame.getGamemode() == gamemode) {
                worldsManager.load(bedwarsGame);
                break;
            }
        }
        loading = false;
    }

    public final Optional<ActiveGame> findOptimalGame(final Gamemode gamemode) {
        Integer diff = null;
        ActiveGame bestGame = null;

        for (final ActiveGame game : worldsManager.getActiveGameList()) {
            if (game.getBedwarsGame().getGamemode() != gamemode) {
                continue;
            }
            if (game.getGameState() == ActiveGameState.STARTED) {
                continue;
            }
            final AbstractQueue<Player> queue = game.getGameLobbyTicker().getAssociatedQueue();
            final int newDiff = game.getBedwarsGame().getPlayers() - queue.queueSize();
            if (diff == null) {
                diff = newDiff;
                bestGame = game;
            } else if (newDiff < diff) {
                diff = newDiff;
                bestGame = game;
            }
        }

        return bestGame == null ? Optional.empty() : Optional.of(bestGame);
    }

    public final void addToQueue(final Player player, final ActiveGame activeGame) {
        final AbstractQueue<Player> queue = activeGame.getGameLobbyTicker().getAssociatedQueue();
        final ConnectToQueueEvent event = new ConnectToQueueEvent(activeGame, player);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        activeGame.getMovementsManager().moveToWaitingRoom(player);
        queue.addToQueue(player);
    }

    public ActiveGame getPlayerCurrentGame(Player player) {
        for (ActiveGame activeGame : this.worldsManager.getActiveGameList()) {
            for (Player p : activeGame.getGameLobbyTicker().getAssociatedQueue().getInQueue()) {
                if (player.equals(p)) {
                    return activeGame;
                }
            }
        }
        return null;
    }

    public final boolean isLoading() {
        return loading;
    }

    public final JavaPlugin getPlugin() {
        return plugin;
    }

    public final WorldsManager getWorldsManager() {
        return worldsManager;
    }

    public final List<BedwarsGame> getBedwarsGameSet() {
        return bedwarsGameSet;
    }

}
