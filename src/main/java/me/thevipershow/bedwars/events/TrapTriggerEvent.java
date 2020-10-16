package me.thevipershow.bedwars.events;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.upgrades.ActiveTrap;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class TrapTriggerEvent extends Event implements Cancellable {

    private boolean cancelled = false;
    public static final HandlerList handlerList = new HandlerList();

    private final Player triggerCause;
    private final ActiveTrap trapTriggered;
    private final BedwarsTeam playerTeam;

    public TrapTriggerEvent(Player triggerCause, ActiveTrap trapTriggered, BedwarsTeam playerTeam) {
        this.triggerCause = triggerCause;
        this.trapTriggered = trapTriggered;
        this.playerTeam = playerTeam;
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

    public final Player getTriggerCause() {
        return triggerCause;
    }

    public final ActiveTrap getTrapTriggered() {
        return trapTriggered;
    }

    public final BedwarsTeam getPlayerTeam() {
        return playerTeam;
    }
}
