package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class UnregisterableListener implements Listener {

    public UnregisterableListener(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    protected boolean isUnregistered = false;

    protected final ActiveGame activeGame;

    public final void unregister() {
        if (isUnregistered) {
            throw new UnsupportedOperationException("Cannot unregister a listener twice.");
        }

        HandlerList.unregisterAll(this);
        isUnregistered = true;
        try {
            finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public boolean isUnregistered() {
        return isUnregistered;
    }

    public void setUnregistered(boolean unregistered) {
        isUnregistered = unregistered;
    }

    public ActiveGame getActiveGame() {
        return activeGame;
    }
}
