package me.thevipershow.bedwars.api;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when a team's bed gets destroyed by a {@link BedwarsPlayer}.
 * Implements {@link Cancellable}, hence can be cancelled to prevent this team's bed being destroyed.
 */
public final class TeamBedDestroyEvent extends ActiveGameEvent implements Cancellable {
    private boolean isCancelled = false;
    public static final HandlerList handlerList = new HandlerList();

    private final BedwarsTeam destroyedTeam;
    private final BedwarsPlayer destroyer;

    /**
     * Event for when a Bed gets broken during Bedwars game.
     *
     * @param activeGame The {@link ActiveGame} instance of the running game.
     * @param team       The team whose bed has been broken.
     * @param destroyer  The {@link BedwarsPlayer} that has broken this bed.
     */
    public TeamBedDestroyEvent(@NotNull ActiveGame activeGame, @NotNull BedwarsTeam team, @NotNull BedwarsPlayer destroyer) {
        super(activeGame);
        this.destroyedTeam = team;
        this.destroyer = destroyer;
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

    /**
     * Get the {@link BedwarsPlayer} that has just destroyed the bed.
     *
     * @return The BedwarsPlayer.
     */
    @NotNull
    public final BedwarsPlayer getDestroyer() {
        return destroyer;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * Get the {@link BedwarsTeam} whose bed has just been broken.
     *
     * @return The BedwarsTeam.
     */
    @NotNull
    public final BedwarsTeam getDestroyedTeam() {
        return destroyedTeam;
    }
}
