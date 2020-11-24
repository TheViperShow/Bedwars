package me.thevipershow.bedwars.api;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event represents a player leaving an ActiveGame
 * during any of its stages.
 */
public final class BedwarsPlayerQuitEvent extends ActiveGameEvent {

    private final static HandlerList handlers = new HandlerList();
    private final BedwarsPlayer bedwarsPlayer;

    /**
     * This event should be built right before the player has left the game.
     *
     * @param activeGame    The ActiveGame that the BedwarsPlayer quit from.
     * @param bedwarsPlayer The BedwarsPlayer that has just quit.
     */
    public BedwarsPlayerQuitEvent(@NotNull ActiveGame activeGame, @NotNull BedwarsPlayer bedwarsPlayer) {
        super(activeGame);
        this.bedwarsPlayer = bedwarsPlayer;
    }

    @Override
    public final HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the BedwarsPlayer that has quit the game.
     *
     * @return The BedwarsPlayer that has quit the game.
     */
    public final BedwarsPlayer getBedwarsPlayer() {
        return bedwarsPlayer;
    }
}
