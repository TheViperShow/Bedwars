package me.thevipershow.bedwars.game.objects;

import java.util.HashSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public final class InvisibilityManager {

    private final ActiveGame activeGame;

    private final HashSet<BedwarsPlayer> hiddenPlayers = new HashSet<>();

    public final void hidePlayer(Player player) {
        BedwarsPlayer bedwarsPlayer = activeGame.getTeamManager().get(player);
        if (bedwarsPlayer != null && !hiddenPlayers.contains(bedwarsPlayer)) {
            hiddenPlayers.add(bedwarsPlayer);
            for (BedwarsPlayer gamePlayer : activeGame.getTeamManager().getGamePlayers()) {
                if (!gamePlayer.getPlayer().equals(player) && gamePlayer.getBedwarsTeam() != bedwarsPlayer.getBedwarsTeam()) {
                    gamePlayer.getPlayer().hidePlayer(player);
                }
            }
        }
    }

    public final void showPlayer(Player player) {
        BedwarsPlayer bedwarsPlayer = activeGame.getTeamManager().get(player);
        if (bedwarsPlayer != null && hiddenPlayers.contains(bedwarsPlayer)) {
            hiddenPlayers.remove(bedwarsPlayer);
            for (BedwarsPlayer gamePlayer : activeGame.getTeamManager().getGamePlayers()) {
                gamePlayer.getPlayer().showPlayer(player);
            }
        }
    }
}
