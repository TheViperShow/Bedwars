package me.thevipershow.aussiebedwars.events;

import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.AbstractQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LeaveQueueEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final AbstractQueue<Player> matchmakingQueue;
    private final ActiveGame activeGame;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     */
    public LeaveQueueEvent(final ActiveGame activeGame) {
        this.matchmakingQueue = activeGame.getAssociatedQueue();
        this.activeGame = activeGame;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public AbstractQueue<Player> getMatchmakingQueue() {
        return matchmakingQueue;
    }

    public ActiveGame getActiveGame() {
        return activeGame;
    }
}
