package me.thevipershow.bedwars.game.objects;

import java.util.EnumMap;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Player;

public abstract class TeamManager<T> {

    public TeamManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private final ActiveGame activeGame;
    private final Map<BedwarsTeam, TeamData<T>> dataMap = new EnumMap<>(BedwarsTeam.class);

    public abstract void assignTeams();

    public final void updateBedwarsPlayersTeam() {
        for (Map.Entry<BedwarsTeam, TeamData<T>> entry : dataMap.entrySet()) {
            BedwarsTeam team = entry.getKey();
            entry.getValue().perform(bedwarsPlayer -> bedwarsPlayer.setBedwarsTeam(team));
        }
    }

    public final void removePlayer(Player player) {
        for (final Map.Entry<BedwarsTeam, TeamData<T>> entry : dataMap.entrySet()) {
            for (final BedwarsPlayer bedwarsPlayer : entry.getValue().getAll()) {
                if (bedwarsPlayer.getUniqueId().equals(player.getUniqueId())) {
                    if (activeGame.getBedwarsGame().getGamemode() == Gamemode.SOLO) {
                        dataMap.remove(entry.getKey());
                    } else {
                        ((MultiTeamData) entry.getValue()).removePlayer(player);
                    }
                    break;
                }
            }
        }
    }

    public ActiveGame getActiveGame() {
        return activeGame;
    }

    public Map<BedwarsTeam, TeamData<T>> getDataMap() {
        return dataMap;
    }

}
