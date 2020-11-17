package me.thevipershow.bedwars.game.objects;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.events.TeamEliminationEvent;
import me.thevipershow.bedwars.events.TeamWinEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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

    public final void setEveryoneStatus(PlayerState status) {
        performAll(bp -> bp.setPlayerState(status));
    }

    public final TeamData<T> dataOfTeam(BedwarsTeam team) {
        return this.dataMap.get(team);
    }

    /**
     * Perform an action to every BedwarsPlayer of the game.
     *
     * @param consumer The action to perform.
     */
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

    /**
     * Check if a team has permanently lost the game and
     * throws a {@link TeamEliminationEvent} when this happens.
     *
     * @param team The {@link BedwarsTeam} to check for.
     */
    public final void checkForTeamLose(BedwarsTeam team) {

        TeamData<T> data = this.dataMap.get(team);

        if (data.getStatus() == TeamStatus.BED_EXISTS) {
            return;
        }

        if (data == null) {
            return;
        }

        boolean teamHasLost = data.getAll().stream().noneMatch(bp -> bp.getPlayerState() != PlayerState.DEAD);

        if (teamHasLost && data.getStatus() == TeamStatus.BED_BROKEN) {
            TeamEliminationEvent teamLoseEvent = new TeamEliminationEvent(activeGame, team);
            activeGame.callGameEvent(teamLoseEvent);
            if (!teamLoseEvent.isCancelled()) {
                data.setStatus(TeamStatus.ELIMINATED);
            }
        }
    }

    /**
     * Check if a team has won the game and
     * throws a {@link TeamWinEvent} when this happens.
     */
    public final void checkForTeamWin() {

        short playingTeamsCount = 0;

        List<BedwarsTeam> result = dataMap.entrySet()
                .stream()
                .filter(e -> e.getValue().getStatus() != TeamStatus.ELIMINATED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (result.size() != 1) {
            return;
        }

        BedwarsTeam winner = result.get(0);

        Plugin plugin = activeGame.getPlugin();
        TeamWinEvent teamWinEvent = new TeamWinEvent(this.activeGame, winner);
        plugin.getServer().getPluginManager().callEvent(teamWinEvent);
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

    public final ActiveGame getActiveGame() {
        return activeGame;
    }

    public final Map<BedwarsTeam, TeamData<T>> getDataMap() {
        return dataMap;
    }

}
