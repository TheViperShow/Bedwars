package me.thevipershow.bedwars.events;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.objects.BedwarsPlayer;
import me.thevipershow.bedwars.game.upgrades.ActiveTrap;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event represents a trap being activated during a game.
 */
public final class TrapTriggerEvent extends ActiveGameEvent implements Cancellable {

    private boolean cancelled = false;
    public static final HandlerList handlerList = new HandlerList();
    private final BedwarsPlayer triggerCause;
    private final ActiveTrap trapTriggered;
    private final BedwarsTeam ownerTeam;

    /**
     * This event should be called when a trap gets correctly activated by an enemy team.
     * @param activeGame The ActiveGame where this has happened.
     * @param triggerCause The BedwarsPlayer that caused the trap to be triggered.
     * @param trapTriggered The ActiveTrap that has been triggered.
     * @param ownerTeam The BedwarsTeam that owned the just activated trap.
     */
    public TrapTriggerEvent(@NotNull ActiveGame activeGame, @NotNull BedwarsPlayer triggerCause, @NotNull ActiveTrap trapTriggered, @NotNull BedwarsTeam ownerTeam) {
        super(activeGame);
        this.triggerCause = triggerCause;
        this.trapTriggered = trapTriggered;
        this.ownerTeam = ownerTeam;
    }

    @Override
    public final HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public final boolean isCancelled() {
        return cancelled;
    }

    @Override
    public final void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Get whoever triggered this trap.
     * @return The BedwarsPlayer that triggered this trap.
     */
    @NotNull
    public final BedwarsPlayer getTriggerCause() {
        return triggerCause;
    }

    /**
     * Get the trap triggered in this event.
     * @return The trap that has just been triggered.
     */
    @NotNull
    public final ActiveTrap getTrapTriggered() {
        return trapTriggered;
    }

    /**
     * Get the team owning the trap
     * @return The trap owner team.
     */
    @NotNull
    public final BedwarsTeam getOwnerTeam() {
        return ownerTeam;
    }
}
