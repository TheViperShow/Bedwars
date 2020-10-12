package me.thevipershow.aussiebedwars.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.thevipershow.aussiebedwars.LoggerUtils;
import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.config.BedwarsGamemodeConfig;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.events.ConnectToQueueEvent;
import me.thevipershow.aussiebedwars.events.LeaveQueueEvent;
import me.thevipershow.aussiebedwars.worlds.WorldsManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class GameManager {

    @SafeVarargs
    public GameManager(final JavaPlugin plugin,
                       final WorldsManager worldsManager,
                       final BedwarsGamemodeConfig<? extends BedwarsGame>... configs) {
        this.plugin = plugin;
        this.worldsManager = worldsManager;
        for (final BedwarsGamemodeConfig<? extends BedwarsGame> config : configs) {
            this.bedwarsGameSet.addAll(config.getBedwarsObjects());
        }
    }

    private final JavaPlugin plugin;
    private final WorldsManager worldsManager;
    private final List<BedwarsGame> bedwarsGameSet = new ArrayList<>();
    private boolean loading = false;

    public void loadBaseAmount() {
        loading = true;
        bedwarsGameSet.forEach(bedwarsGame -> {
            int count = 0;
            while (count < bedwarsGame.getMinGames()) {
                worldsManager.load(bedwarsGame);
                count++;
            }
        });
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

    public Optional<ActiveGame> findOptimalGame(final Gamemode gamemode) {
        Integer diff = null;
        ActiveGame bestGame = null;

        for (final ActiveGame game : worldsManager.getActiveGameList()) {
            if (game.bedwarsGame.getGamemode() != gamemode) continue;
            if (game.isHasStarted()) continue;
            final AbstractQueue<Player> queue = game.getAssociatedQueue();
            final int newDiff = game.bedwarsGame.getPlayers() - queue.queueSize();
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

    public boolean addToQueue(final Player player, final ActiveGame activeGame) {
        final AbstractQueue<Player> queue = activeGame.getAssociatedQueue();
        final ConnectToQueueEvent event = new ConnectToQueueEvent(activeGame);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;
        activeGame.moveToWaitingRoom(player);
        queue.addToQueue(player);
        return true;
    }

    public void removeFromAllQueues(final Player player) {
        worldsManager.getActiveGameList().forEach(b -> {
            final AbstractQueue<Player> queue = b.getAssociatedQueue();
            if (queue.contains(player)) {
                final LeaveQueueEvent leaveQueueEvent = new LeaveQueueEvent(b);
                queue.removeFromQueue(player);
                plugin.getServer().getPluginManager().callEvent(leaveQueueEvent);
            }
        });
    }

    public boolean isLoading() {
        return loading;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public final WorldsManager getWorldsManager() {
        return worldsManager;
    }

    public final List<BedwarsGame> getBedwarsGameSet() {
        return bedwarsGameSet;
    }

}
