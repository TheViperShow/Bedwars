package me.thevipershow.bedwars.api;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event represents the 'death' of a so called BedwarsPlayer.
 * It is called just before his death and is cancellable.
 */
public class BedwarsPlayerDeathEvent extends ActiveGameEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false; // cancellable logic

    private final BedwarsPlayer died;   //
    private final BedwarsPlayer killer; //
    private final DamageCause cause;    //
    private final Entity killerEntity;  //
    private final boolean isFinalKill;  //

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     */
    public BedwarsPlayerDeathEvent(
            @NotNull ActiveGame activeGame,
            @NotNull EntityDamageEvent.DamageCause cause,
            @NotNull BedwarsPlayer died,
            @Nullable Entity killerEntity,
            @Nullable BedwarsPlayer killer,
            boolean isFinalKill) {
        super(activeGame);
        this.died = died;
        this.isFinalKill = isFinalKill;
        this.cause = cause;
        this.killerEntity = killerEntity;
        this.killer = killer;
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
    public final HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the BedwarsPlayer that has been killed.
     *
     * @return The BedwarsPlayer.
     */
    @NotNull
    public final BedwarsPlayer getDied() {
        return this.died;
    }

    /**
     * Get whether the kill was final or not.
     * Final kills happen when the
     * team's bed has been previously broken.
     *
     * @return If the kill is final or not.
     */
    public final boolean isFinalKill() {
        return this.isFinalKill;
    }

    /**
     * Get the reason that caused the BedwarsPlayer to get killed.
     *
     * @return The DamageCause.
     */
    @NotNull
    public final DamageCause getCause() {
        return cause;
    }

    /**
     * Get the BedwarsPlayer that caused the dead player to die.
     *
     * @return A BedwarsPlayer if the dead has been killed
     * by another BedwarsPlayer, null otherwise.
     */
    @Nullable
    public final BedwarsPlayer getKiller() {
        return this.killer;
    }

    /**
     * Entity responsible for killing the BedwarsPlayer
     * @return The Entity that killed the BedwarsPlayer.
     *
     * This is null when the player was not killed by an entity.
     * In case this is not null, and the player got killed by another player,
     * it will return the same player entity from {@link BedwarsPlayer#getPlayer()}
     */
    @Nullable
    public final Entity getKillerEntity() {
        return killerEntity;
    }
}
