package me.thevipershow.bedwars.game.objects;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.Gamemode;
import org.bukkit.entity.Player;

public final class SoloTeamData extends TeamData<BedwarsPlayer> {

    public SoloTeamData(Gamemode gamemode, PlayerMapper playerMapper) {
        super(gamemode, playerMapper);
    }

    @Override
    public final void add(Player player) {
        setData(getPlayerMapper().get(player));
    }

    @Override
    public final void perform(Consumer<? super BedwarsPlayer> consumer) {
        consumer.accept(getData());
    }

    @Override
    public final Set<BedwarsPlayer> getAll() {
        return Collections.singleton(getData());
    }

    @Override
    public final String getStatusCharacter() {
        switch (getStatus()) {
            case BED_EXISTS:
                return AllStrings.GRAPHIC_TICK.get();
            case BED_BROKEN:
                return "§f§l1";
            default:
                return AllStrings.GRAPHIC_CROSS.get();
        }
    }
}
