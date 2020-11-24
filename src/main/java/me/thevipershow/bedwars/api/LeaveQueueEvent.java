package me.thevipershow.bedwars.api;

import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LeaveQueueEvent extends ActiveGameEvent {

    private static final HandlerList handlerList = new HandlerList();
    private final Player player;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     */
    public LeaveQueueEvent(@NotNull ActiveGame activeGame, @NotNull Player player) {
        super(activeGame);
        this.player = player;
    }

    /**
     * Get the player that left the queue.
     * @return The player.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
