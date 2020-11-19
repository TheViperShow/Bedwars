package me.thevipershow.bedwars.api;

import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class ActiveGameTerminateEvent extends ActiveGameEvent {
    private static HandlerList handlers = new HandlerList();

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     *
     * @param activeGame
     */
    public ActiveGameTerminateEvent(@NotNull ActiveGame activeGame) {
        super(activeGame);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
