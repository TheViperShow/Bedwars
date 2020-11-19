package me.thevipershow.bedwars.listeners.global;

import java.util.Objects;
import java.util.Set;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.api.ActiveGameEvent;
import me.thevipershow.bedwars.api.BedwarsPlayerQuitEvent;
import me.thevipershow.bedwars.api.TeamEliminationEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.managers.GameManager;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.game.PlayerMapper;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import me.thevipershow.bedwars.game.managers.TeamManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QueueResizerListener implements Listener {

    private final GameManager gameManager;

    public QueueResizerListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler(ignoreCancelled = true)
    public final void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ActiveGame activeGame = gameManager.getPlayerCurrentGame(player);
        if (activeGame == null) {
            return;
        }

        activeGame.getGameLobbyTicker().getAssociatedQueue().removeFromQueue(player);

        PlayerMapper playerMapper = activeGame.getPlayerMapper();
        BedwarsPlayer bedwarsPlayer = Objects.requireNonNull(playerMapper.get(player));
        ActiveGameEvent bedwarsPlayerQuitEvent = new BedwarsPlayerQuitEvent(activeGame, bedwarsPlayer);

        activeGame.callGameEvent(bedwarsPlayerQuitEvent);

        playerMapper.getMappings().remove(player.getUniqueId());
        removeBedwarsPlayerData(activeGame, bedwarsPlayer);
    }

    private void removeBedwarsPlayerData(ActiveGame activeGame, BedwarsPlayer bedwarsPlayer) {
        TeamManager<?> teamManager = activeGame.getTeamManager();
        BedwarsTeam team = bedwarsPlayer.getBedwarsTeam();
        boolean removeTeam = false;
        if (activeGame.getBedwarsGame().getGamemode() == Gamemode.SOLO) {
            removeTeam = true;
        } else {
            TeamData<?> teamData = teamManager.getDataMap().get(team);
            Set<BedwarsPlayer> remainingBedwarsPlayers = teamData.getAll();
            remainingBedwarsPlayers.remove(bedwarsPlayer);
            if (remainingBedwarsPlayers.size() == 0) {
                removeTeam = true;
            }
        }

        if (removeTeam) {
            activeGame.callGameEvent(new TeamEliminationEvent(activeGame, team, TeamEliminationEvent.EliminationCause.QUIT));
            teamManager.getDataMap().remove(team);
        }
    }
}
