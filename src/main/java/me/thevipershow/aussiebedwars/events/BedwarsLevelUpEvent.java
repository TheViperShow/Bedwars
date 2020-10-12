package me.thevipershow.aussiebedwars.events;

import me.thevipershow.aussiebedwars.game.ActiveGame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class BedwarsLevelUpEvent extends Event implements Cancellable {

    private boolean cancel = false;
    public static HandlerList handler = new HandlerList();

    private final Player player;
    private final int oldLevel, newLevel;
    private final ActiveGame playingIn;

    public BedwarsLevelUpEvent(final Player player, final int oldLevel, final int newLevel, final ActiveGame playingIn) {
        this.player = player;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.playingIn = playingIn;
    }

    @Override
    public final boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public final void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public final HandlerList getHandlers() {
        return handler;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }

    public final boolean isCancel() {
        return cancel;
    }

    public final Player getPlayer() {
        return player;
    }

    public final int getOldLevel() {
        return oldLevel;
    }

    public final int getNewLevel() {
        return newLevel;
    }

    public final ActiveGame getPlayingIn() {
        return playingIn;
    }
}
