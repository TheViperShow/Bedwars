package me.thevipershow.bedwars.events;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class TeamBedDestroyEvent extends Event implements Cancellable {
    private boolean isCancelled = false;
    public static final HandlerList handlerList = new HandlerList();

    private final ActiveGame activeGame;
    private final BedwarsTeam team;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     */
    public TeamBedDestroyEvent(final ActiveGame activeGame, final BedwarsTeam team) {
        this.activeGame = activeGame;
        this.team = team;
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

    public ActiveGame getActiveGame() {
        return activeGame;
    }

    public BedwarsTeam getTeam() {
        return team;
    }
}
