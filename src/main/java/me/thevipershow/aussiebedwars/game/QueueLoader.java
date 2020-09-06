package me.thevipershow.aussiebedwars.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.listeners.AbstractQueue;
import me.thevipershow.aussiebedwars.listeners.MatchmakingQueue;
import org.bukkit.entity.Player;

public class QueueLoader {

    private final HashMap<BedwarsGame, AbstractQueue<Player>> activeQueues = new HashMap<>();
    private final Set<BedwarsGame> games;

    public QueueLoader(Set<BedwarsGame> games) {
        this.games = games;
    }

    public AbstractQueue<Player> getPlayerQueue(final Player player) {
        for (final AbstractQueue<Player> queue : activeQueues.values()) {
            if (queue.contains(player)) return queue;
        }
        return null;
    }

    public void addToQueue(final Player player, final BedwarsGame bedwarsGame) {
        AbstractQueue<Player> q = activeQueues.get(bedwarsGame);
        if (q != null)
            q.addToQueue(player);
    }

    public Optional<BedwarsGame> findOptimalGame(final Gamemode gamemode) {

        BedwarsGame optimal = null;
        int missingSlots = 0;

        for (Map.Entry<BedwarsGame, AbstractQueue<Player>> entry : activeQueues.entrySet()) {
            final BedwarsGame key = entry.getKey();
            if (key.getGamemode() != gamemode) continue;
            final AbstractQueue<Player> value = entry.getValue();
            final int missing = key.getPlayers() - value.queueSize();
            if (optimal == null) {
                optimal = key;
                missingSlots = missing;
                continue;
            }
            if (missing > missingSlots)
                optimal = key;
        }

        return optimal == null ? Optional.empty() : Optional.of(optimal);

        //return activeQueues.entrySet().stream()
        //        .filter(e -> e.getKey().getGamemode() == gamemode)
        //        .reduce((a, b) -> a.getKey().getPlayers() - a.getValue().queueSize() > b.getKey().getPlayers() - b.getValue().queueSize() ? a : b)
        //        .map(Map.Entry::getKey);
    }

    public boolean removeFromQueues(final Player player) {
        final AbstractQueue<Player> playerQueue = getPlayerQueue(player);
        if (playerQueue == null) return false;
        playerQueue.removeFromQueue(player);
        return true;
    }

    public void loadGames() {
        games.forEach(g -> this.activeQueues.put(g, new MatchmakingQueue(g.getPlayers())));
    }

    public boolean unloadGame(final BedwarsGame game) {
        return this.activeQueues.remove(game) != null;
    }

    public AbstractQueue<? extends Player> getGameQueue(final BedwarsGame game) {
        return this.activeQueues.get(game);
    }
}
