package me.thevipershow.bedwars.events;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.AbstractQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ConnectToQueueEvent extends Event implements Cancellable {

    private boolean isCancelled = false;
    public static final HandlerList handlerList = new HandlerList();
    private final AbstractQueue<Player> matchmakingQueue;
    private final ActiveGame activeGame;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     */
    public ConnectToQueueEvent(final ActiveGame activeGame) {
        this.matchmakingQueue = activeGame.getGameLobbyTicker().getAssociatedQueue();
        this.activeGame = activeGame;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(final boolean cancel) {
        this.isCancelled = cancel;
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
