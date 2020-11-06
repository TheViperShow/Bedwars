package me.thevipershow.bedwars.listeners;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
@Data
public abstract class UnregisterableListener implements Listener {

    protected boolean isUnregistered = false;

    protected final ActiveGame activeGame;

    public final void unregister() {
        if (isUnregistered) {
            throw new UnsupportedOperationException("Cannot unregister a listener twice.");
        }

        HandlerList.unregisterAll(this);
        isUnregistered = true;
    }

}
