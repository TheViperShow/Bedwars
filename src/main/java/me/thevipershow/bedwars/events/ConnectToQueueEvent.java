package me.thevipershow.bedwars.events;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.AbstractQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event represents a connection to a game's queue.
 * An online player is always involved in it.
 */
public final class ConnectToQueueEvent extends ActiveGameEvent implements Cancellable {

    public static final HandlerList handlerList = new HandlerList();
    private final AbstractQueue<Player> matchmakingQueue;
    private final Player connected;
    private boolean isCancelled = false;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     */
    public ConnectToQueueEvent(@NotNull ActiveGame activeGame, @NotNull Player connected) {
        super(activeGame);
        this.connected = connected;
        this.matchmakingQueue = activeGame.getGameLobbyTicker().getAssociatedQueue();
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

    @NotNull
    public AbstractQueue<Player> getMatchmakingQueue() {
        return matchmakingQueue;
    }

    @NotNull
    public final Player getConnected() {
        return connected;
    }
}
