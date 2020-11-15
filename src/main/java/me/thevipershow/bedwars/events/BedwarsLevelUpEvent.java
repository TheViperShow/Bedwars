package me.thevipershow.bedwars.events;

import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class BedwarsLevelUpEvent extends ActiveGameEvent implements Cancellable {

    private boolean cancel = false;
    public static HandlerList handler = new HandlerList();

    private final Player player;
    private final int oldLevel, newLevel;

    public BedwarsLevelUpEvent(@NotNull Player player, int oldLevel, int newLevel, @NotNull ActiveGame activeGame) {
        super(activeGame);
        this.player = player;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
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

    @NotNull
    public final Player getPlayer() {
        return player;
    }

    public final int getOldLevel() {
        return oldLevel;
    }

    public final int getNewLevel() {
        return newLevel;
    }
}
