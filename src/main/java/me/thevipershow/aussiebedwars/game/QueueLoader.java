package me.thevipershow.aussiebedwars.game;

import java.util.HashMap;
import java.util.Set;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.listeners.AbstractQueue;
import me.thevipershow.aussiebedwars.listeners.MatchmakingQueue;
import org.bukkit.entity.Player;

public class QueueLoader {

    private final HashMap<BedwarsGame, AbstractQueue<? extends Player>> activeQueues = new HashMap<>();
    private final Set<BedwarsGame> games;

    public QueueLoader(Set<BedwarsGame> games) {
        this.games = games;
    }

    public void loadGames() {
        games.forEach(g -> this.activeQueues.put(g, new MatchmakingQueue(g.getPlayers())));
    }

    public boolean unloadGame(final BedwarsGame game) {
        return this.activeQueues.remove(game) != null;
    }

    // might be null
    public AbstractQueue<? extends Player> getGameQueue(final BedwarsGame game) {
        return this.activeQueues.get(game);
    }
}
