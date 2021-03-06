package me.thevipershow.bedwars.game.data.teams.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.game.PlayerMapper;
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
        return this.data;
    }

    @Override
    public String getStatusCharacter() {
        switch (getStatus()) {
            case BED_EXISTS:
                return AllStrings.GRAPHIC_TICK.get();
            case BED_BROKEN:
                return "§f§l" + getData().size();
            default:
                return AllStrings.GRAPHIC_CROSS.get();
        }
    }
}
