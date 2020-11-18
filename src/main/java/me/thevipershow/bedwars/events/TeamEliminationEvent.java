package me.thevipershow.bedwars.events;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event represents the loss of a game by a team.
 */
public final class TeamEliminationEvent extends ActiveGameEvent implements Cancellable {

    public enum EliminationCause {
        DEATH, QUIT
    }

    private static final HandlerList handlers = new HandlerList();
    private final BedwarsTeam bedwarsTeam;
    private boolean cancelled = false;
    private final EliminationCause eliminationCause;

    public TeamEliminationEvent(@NotNull ActiveGame activeGame, @NotNull BedwarsTeam bedwarsTeam, @NotNull EliminationCause eliminationCause) {
        super(activeGame);
        this.bedwarsTeam = bedwarsTeam;
        this.eliminationCause = eliminationCause;
    }

    /**
     * Get the team that has just been eliminated from the game.
     *
     * @return The team that has lost this game.
     */
    @NotNull
    public final BedwarsTeam getBedwarsTeam() {
        return bedwarsTeam;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
