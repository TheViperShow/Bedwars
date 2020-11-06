package me.thevipershow.bedwars.game.objects;

import java.util.HashSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public abstract class TeamManager {

    private final ActiveGame activeGame;

    private final HashSet<BedwarsPlayer> gamePlayers = new HashSet<>();

    public abstract void assignTeams();

    public BedwarsPlayer get(Player player) {
        for (BedwarsPlayer gamePlayer : gamePlayers) {
            if (gamePlayer.getPlayer().equals(player)) {
                return gamePlayer;
            }
        }
        return null;
    }
}
