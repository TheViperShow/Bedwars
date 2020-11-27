package me.thevipershow.bedwars.game.managers;

import java.util.HashSet;
import java.util.Set;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.teams.TeamData;

public final class InvisibilityManager extends AbstractGameManager {

    public InvisibilityManager(ActiveGame activeGame) {
        super(activeGame);
    }

    public final void hidePlayer(BedwarsPlayer player) {
        for (TeamData<?> value : activeGame.getTeamManager().getDataMap().values()) {
            value.perform(bedwarsPlayer -> {
                if (bedwarsPlayer.equals(player)) {
                    bedwarsPlayer.setHidden(true);
                } else {
                    bedwarsPlayer.getPlayer().hidePlayer(player.getPlayer());
                }
            });
        }
    }

    public final void showPlayer(BedwarsPlayer player) {
        for (TeamData<?> value : activeGame.getTeamManager().getDataMap().values()) {
            value.perform(bedwarsPlayer -> {
                if (bedwarsPlayer.equals(player)) {
                    bedwarsPlayer.setHidden(false);
                } else {
                    bedwarsPlayer.getPlayer().showPlayer(player.getPlayer());
                }
            });
        }
    }

    public final Set<BedwarsPlayer> getHiddenPlayers() {
        final Set<BedwarsPlayer> set = new HashSet<>();
        for (BedwarsPlayer value : activeGame.getInternalGameManager().getPlayerMapper().getMappings().values()) {
            if (value.isHidden()) {
                set.add(value);
            }
        }
        return set;
    }
}
