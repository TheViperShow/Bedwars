package me.thevipershow.bedwars.api;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is called whenever a player has enough experience
 * to progress to his next level.
 * This event is cancellable and doing so will result in the player
 * not reaching his next level.
 */
public final class BedwarsLevelUpEvent extends ActiveGameEvent implements Cancellable {

    private boolean cancel = false;
    public static HandlerList handler = new HandlerList();

    private final BedwarsPlayer player;
    private final int oldLevel, newLevel;

    public BedwarsLevelUpEvent(@NotNull BedwarsPlayer player, int oldLevel, int newLevel, @NotNull ActiveGame activeGame) {
        super(activeGame);
        this.player = player;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public final boolean isCancelled() {
        return this.cancel;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public final void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public final HandlerList getHandlers() {
        return handler;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }

    /**
     * Get the BedwarsPlayer that is going to reach the next level.
     *
     * @return The BedwarsPlayer.
     */
    @NotNull
    public final BedwarsPlayer getPlayer() {
        return player;
    }

    /**
     * Get the level that this player had before reaching the next one.
     * It is not granted that the previous level is the one before
     * the next, the player could have increased for some reasons by more
     * than 1 levels at a time, resulting in different scales.
     *
     * @return The previous level.
     */
    public final int getOldLevel() {
        return oldLevel;
    }

    /**
     * Get the level that this player reached after leveling up.
     * @return The next level.
     */
    public final int getNewLevel() {
        return newLevel;
    }
}
