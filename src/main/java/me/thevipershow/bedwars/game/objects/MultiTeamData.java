package me.thevipershow.bedwars.game.objects;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.Gamemode;
import org.bukkit.entity.Player;

public final class MultiTeamData extends TeamData<Set<BedwarsPlayer>> {

    public MultiTeamData(Gamemode gamemode, PlayerMapper playerMapper) {
        super(gamemode, playerMapper);
        setData(new HashSet<>());
    }

    @Override
    public final void add(Player player) {
        getData().add(getPlayerMapper().get(player));
    }

    @Override
    public final void perform(Consumer<? super BedwarsPlayer> consumer) {
        for (BedwarsPlayer bedwarsPlayer : getData()) {
            consumer.accept(bedwarsPlayer);
        }
    }

    public final void removePlayer(BedwarsPlayer player) {
        getData().remove(player);
    }

    public final void removePlayer(Player player) {
        BedwarsPlayer bedwarsPlayer = getPlayerMapper().get(player);
        if (bedwarsPlayer != null) {
            removePlayer(bedwarsPlayer);
        }
    }

    @Override
    public final Set<BedwarsPlayer> getAll() {
        return getData();
    }

    @Override
    public void updateStatusCharacter() {
        switch (getStatus()) {
            case BED_EXISTS:
                setStatusCharacter(AllStrings.GRAPHIC_TICK.get());
                break;
            case BED_BROKEN:
                setStatusCharacter("§f§l" + getData().size());
                break;
            default:
                setStatusCharacter(AllStrings.GRAPHIC_CROSS.get());
                break;
        }
    }
}
