package me.thevipershow.bedwars.game.objects;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.events.TeamLoseEvent;
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

    public final TeamData<T> dataOfTeam(BedwarsTeam team) {
        return this.dataMap.get(team);
    }

    public final void performAll(final Consumer<? super BedwarsPlayer> consumer) {
        for (final TeamData<T> data : dataMap.values()) {
            data.perform(consumer);
        }
    }

    /**
     * Send a message to every playing BedwarsPlayer.
     *
     * @param msg The message to send.
     */
    public final void sendMessageToAll(String msg) {
        performAll(bp -> bp.sendMessage(msg));
    }

    public final void checkForTeamLose(BedwarsTeam team) {
        for (Map.Entry<BedwarsTeam, TeamData<T>> entry : dataMap.entrySet()) {
            BedwarsTeam key = entry.getKey();
            if (key != team) {
                continue;
            }
            TeamData<T> teamData = entry.getValue();
            if (teamData.getAll().stream().map(BedwarsPlayer::getPlayerState).allMatch(state -> state != PlayerState.PLAYING && state != PlayerState.RESPAWNING) && teamData.getStatus() != TeamStatus.ELIMINATED) {
                TeamLoseEvent teamLoseEvent = new TeamLoseEvent(this.activeGame, key);
                activeGame.getPlugin().getServer().getPluginManager().callEvent(teamLoseEvent);
                break;
            }
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

    public final TeamData<?> dataOfBedwarsPlayer(BedwarsPlayer bedwarsPlayer) {
        for (Map.Entry<BedwarsTeam, TeamData<T>> entry : dataMap.entrySet()) {
            if (bedwarsPlayer.getBedwarsTeam() == entry.getKey()) {
                return entry.getValue();
            }
        }
        return null;
    }

    public ActiveGame getActiveGame() {
        return activeGame;
    }

    public Map<BedwarsTeam, TeamData<T>> getDataMap() {
        return dataMap;
    }

}
