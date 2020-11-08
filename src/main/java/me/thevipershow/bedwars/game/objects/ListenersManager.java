package me.thevipershow.bedwars.game.objects;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.plugin.Plugin;


public final class ListenersManager {

    private final ActiveGame activeGame;

    private final Map<UnregisterableListener, Boolean> unregisterableListeners = new HashMap<>();
    private final Map<GameListener, UnregisterableListener> gameListenerMap = new EnumMap<>(GameListener.class);

    public ListenersManager(ActiveGame activeGame) {
        this.activeGame = activeGame;

        for (GameListener value : GameListener.values()) {
            UnregisterableListener unregisterableListener = value.newInstance(activeGame);
            gameListenerMap.put(value, unregisterableListener);
            this.unregisterableListeners.put(unregisterableListener, false);
        }
    }

    public final void disableListener(GameListener gameListener) {
        UnregisterableListener unregisterableListener = gameListenerMap.get(gameListener);
        if (!unregisterableListener.isUnregistered()) {
            unregisterableListener.unregister();
        }
    }

    public final void enableListener(GameListener gameListener) {
        UnregisterableListener unregisterableListener = gameListenerMap.get(gameListener);
        if (unregisterableListener != null && !unregisterableListeners.get(unregisterableListener)) {
            Plugin plugin = activeGame.getPlugin();
            plugin.getServer().getPluginManager().registerEvents(unregisterableListener, plugin);
        }
    }

    public final void enableListeners(GameListener... gameListeners) {
        for (GameListener gameListener : gameListeners) {
            enableListener(gameListener);
        }
    }

    public final void enableAllListeners() {
        enableListeners(GameListener.values());
    }

    public final void disableAllListeners() {
        for (UnregisterableListener unregisterableListener : unregisterableListeners.keySet()) {
            unregisterableListener.unregister();
        }
    }

    public final ActiveGame getActiveGame() {
        return activeGame;
    }

    public final Map<UnregisterableListener, Boolean> getUnregisterableListeners() {
        return unregisterableListeners;
    }

    public final Map<GameListener, UnregisterableListener> getGameListenerMap() {
        return gameListenerMap;
    }
}
