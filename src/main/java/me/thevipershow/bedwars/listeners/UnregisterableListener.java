package me.thevipershow.bedwars.listeners;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class UnregisterableListener implements Listener {

    protected boolean isUnregistered = false;

    public final void unregister() {
        if (isUnregistered)
            throw new UnsupportedOperationException("Cannot unregister a listener twice.");

        HandlerList.unregisterAll(this);
        this.isUnregistered = true;
    }

    public boolean isUnregistered() {
        return isUnregistered;
    }
}
