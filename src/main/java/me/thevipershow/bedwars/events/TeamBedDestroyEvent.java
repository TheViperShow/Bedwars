package me.thevipershow.bedwars.events;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.objects.BedwarsPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class TeamBedDestroyEvent extends ActiveGameEvent implements Cancellable {
    private boolean isCancelled = false;
    public static final HandlerList handlerList = new HandlerList();

    private final BedwarsTeam destroyedTeam;
    private final BedwarsPlayer destroyer;

    /**
     * Event for when a Bed gets broken during Bedwars game.
     * @param activeGame The ActiveGame instance of the running game.
     * @param team The team whose bed has been broken.
     * @param destroyer The BedwarsPlayer that has broken this bed.
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

    @NotNull
    public final BedwarsPlayer getDestroyer() {
        return destroyer;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    public final BedwarsTeam getDestroyedTeam() {
        return destroyedTeam;
    }
}
