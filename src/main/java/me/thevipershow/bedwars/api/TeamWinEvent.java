package me.thevipershow.bedwars.api;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event represents the win of a game by a BedwarsTeam.
 * Hence you can listen for this event if you want to find out
 * when a game has finished.
 * It should only be fired when a team wins and no other enemies
 * are left to play against.
 * This event is cancellable and cancelling it will influence the game.
 */
public final class TeamWinEvent extends ActiveGameEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final BedwarsTeam bedwarsTeam;
    private boolean cancelled = false;


    /**
     * Constructor for the TeamWinEvent class
     *
     * @param activeGame  The ActiveGame where the winning has happened.
     * @param bedwarsTeam The BedwarsTeam that has won.
     */
    public TeamWinEvent(@NotNull ActiveGame activeGame, @NotNull BedwarsTeam bedwarsTeam) {
        super(activeGame);
        this.bedwarsTeam = bedwarsTeam;
    }

    /**
     * Gets the BedwarsTeam that has won this game.
     *
     * @return The Bedwars team that has won the game.
     */
    @NotNull
    public final BedwarsTeam getBedwarsTeam() {
        return bedwarsTeam;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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
