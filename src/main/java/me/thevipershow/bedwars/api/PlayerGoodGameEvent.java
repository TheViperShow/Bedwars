package me.thevipershow.bedwars.api;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class PlayerGoodGameEvent extends ActiveGameEvent implements Cancellable {

    public static HandlerList handlerList = new HandlerList();
    private boolean cancelled = false;
    private final BedwarsPlayer bedwarsPlayer;
    private final String message;

    public PlayerGoodGameEvent(@NotNull ActiveGame activeGame, BedwarsPlayer bedwarsPlayer, String message) {
        super(activeGame);
        this.bedwarsPlayer = bedwarsPlayer;
        this.message = message;
    }

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
        this.cancelled = cancel;
    }

    @Override
    public final HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public final BedwarsPlayer getBedwarsPlayer() {
        return bedwarsPlayer;
    }

    public final String getMessage() {
        return message;
    }
}
