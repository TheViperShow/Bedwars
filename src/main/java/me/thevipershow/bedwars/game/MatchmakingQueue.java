package me.thevipershow.bedwars.game;

import org.bukkit.entity.Player;

public final class MatchmakingQueue extends AbstractQueue<Player> {

    public MatchmakingQueue(int maximumSize) {
        super(maximumSize);
    }
}
