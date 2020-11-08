package me.thevipershow.bedwars.game.objects;

import java.util.HashSet;
import java.util.Set;
import me.thevipershow.bedwars.game.ActiveGame;

public final class InvisibilityManager {

    public InvisibilityManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private final ActiveGame activeGame;

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
