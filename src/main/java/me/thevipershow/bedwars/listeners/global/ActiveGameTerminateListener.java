package me.thevipershow.bedwars.listeners.global;

import me.thevipershow.bedwars.api.ActiveGameTerminateEvent;
import me.thevipershow.bedwars.worlds.WorldsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class ActiveGameTerminateListener implements Listener {

    private final WorldsManager worldsManager;

    public ActiveGameTerminateListener(WorldsManager worldsManager) {
        this.worldsManager = worldsManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onActiveGameTerminate(ActiveGameTerminateEvent event) {
        worldsManager.getActiveGameList().remove(event.getActiveGame());
    }
}
