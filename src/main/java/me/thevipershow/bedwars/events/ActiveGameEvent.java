package me.thevipershow.bedwars.events;

import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the base Event for all of the Events that are thrown from a Game circumstance.
 */
public abstract class ActiveGameEvent extends Event {

    private final ActiveGame activeGame; // the game object.

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     */
    public ActiveGameEvent(@NotNull ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    /**
     * This method returns the ActiveGame that interested this event.
     * @return The ActiveGame.
     */
    @NotNull
    public final ActiveGame getActiveGame() {
        return activeGame;
    }
}
