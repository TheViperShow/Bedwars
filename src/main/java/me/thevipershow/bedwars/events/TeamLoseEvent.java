package me.thevipershow.bedwars.events;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class TeamLoseEvent extends ActiveGameEvent {
    private static final HandlerList handlers = new HandlerList();
    private final BedwarsTeam bedwarsTeam;

    public TeamLoseEvent(@NotNull ActiveGame activeGame, @NotNull BedwarsTeam bedwarsTeam) {
        super(activeGame);
        this.bedwarsTeam = bedwarsTeam;
    }

    @NotNull
    public final BedwarsTeam getBedwarsTeam() {
        return bedwarsTeam;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
