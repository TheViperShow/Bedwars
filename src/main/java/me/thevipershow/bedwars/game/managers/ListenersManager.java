package me.thevipershow.bedwars.game.managers;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.GameListener;
import me.thevipershow.bedwars.listeners.unregisterable.UnregisterableListener;

public final class ListenersManager {

    private final ActiveGame activeGame;

    private final Map<UnregisterableListener, Boolean> unregisterableListeners = new HashMap<>();
    private final Map<GameListener, UnregisterableListener> gameListenerMap = new EnumMap<>(GameListener.class);

    public ListenersManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    public final void enableAllByPhase(GameListener.RegistrationStage stage) {
        for (GameListener listener : GameListener.values()) {
            if (listener.getRegistrationStage() == stage && !this.gameListenerMap.containsKey(listener)) {
                final UnregisterableListener newInstance = listener.newInstance(this.activeGame);
                this.activeGame.getPlugin().getServer().getPluginManager().registerEvents(newInstance, activeGame.getPlugin());
                this.gameListenerMap.put(listener, newInstance);
            }
        }
    }

    public final void disable(GameListener gameListener) {
        UnregisterableListener unregisterableListener = this.gameListenerMap.get(gameListener);
        if (unregisterableListener != null) {
            unregisterableListener.unregister();
            this.gameListenerMap.remove(gameListener);
        }
    }

    public final void disableAllByPhase(GameListener.RegistrationStage stage) {
        for (GameListener listener : GameListener.values()) {
            if (this.gameListenerMap.containsKey(listener)) {
                UnregisterableListener instance = this.gameListenerMap.get(listener);
                if (instance != null && !instance.isUnregistered()) {
                    instance.unregister();
                    this.gameListenerMap.remove(listener);
                }
            }
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
