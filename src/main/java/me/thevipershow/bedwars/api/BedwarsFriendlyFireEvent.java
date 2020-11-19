package me.thevipershow.bedwars.api;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class BedwarsFriendlyFireEvent extends ActiveGameEvent implements Cancellable {
    private static final HandlerList handler = new HandlerList();
    private final BedwarsPlayer attacker, attacked;
    private boolean cancelled = false;

    public BedwarsFriendlyFireEvent(@NotNull ActiveGame activeGame, @NotNull BedwarsPlayer attacker, @NotNull BedwarsPlayer attacked) {
        super(activeGame);
        this.attacker = attacker;
        this.attacked = attacked;
    }

    @Override
    public final HandlerList getHandlers() {
        return handler;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }

    public final BedwarsPlayer getAttacker() {
        return this.attacker;
    }

    public final BedwarsPlayer getAttacked() {
        return this.attacked;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public final boolean isCancelled() {
        return this.cancelled;
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
}
