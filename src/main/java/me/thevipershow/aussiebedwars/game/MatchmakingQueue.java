package me.thevipershow.aussiebedwars.game;

import me.thevipershow.aussiebedwars.game.AbstractQueue;
import org.bukkit.entity.Player;

public final class MatchmakingQueue extends AbstractQueue<Player> {

    public MatchmakingQueue(int maximumSize) {
        super(maximumSize);
    }
}
