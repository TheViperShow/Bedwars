package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.events.TeamEliminationEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.objects.TeamManager;
import me.thevipershow.bedwars.game.objects.TeamStatus;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.event.EventHandler;

public final class TeamEliminationUnregisterableListener extends UnregisterableListener {

    public TeamEliminationUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onTeamLose(TeamEliminationEvent event) {
        BedwarsTeam losers = event.getBedwarsTeam();
        ActiveGame game = event.getActiveGame();

        if (!game.equals(this.activeGame)) {
            return;
        }
        System.out.println("Team lost " + losers.name());

        TeamManager<?> teamManager = activeGame.getTeamManager();
        teamManager.dataOfTeam(losers).setStatus(TeamStatus.ELIMINATED);
        teamManager.checkForTeamWin();
    }
}
