package me.thevipershow.bedwars.api;

import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class AbstractDeathmatchStartEvent extends ActiveGameEvent implements Cancellable {
    public AbstractDeathmatchStartEvent(@NotNull ActiveGame activeGame) {
        super(activeGame);
    }

    public final static HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public final boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public final void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public final HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
