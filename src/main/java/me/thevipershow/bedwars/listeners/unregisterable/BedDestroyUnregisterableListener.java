package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.events.TeamBedDestroyEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.objects.BedwarsPlayer;
import me.thevipershow.bedwars.game.objects.TeamData;
import me.thevipershow.bedwars.game.objects.TeamManager;
import me.thevipershow.bedwars.game.objects.TeamStatus;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;

public final class BedDestroyUnregisterableListener extends UnregisterableListener {
    public BedDestroyUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onTeamBedDestroy(TeamBedDestroyEvent event) {
        BedwarsTeam team = event.getDestroyedTeam();
        if (!event.getActiveGame().equals(activeGame)) {
            return;
        }

        BedwarsPlayer brokenBy = event.getDestroyer();

        TeamManager<?> teamManager = activeGame.getTeamManager();
        TeamData<?> data = teamManager.dataOfTeam(team);
        data.setStatus(TeamStatus.BED_BROKEN);

        data.perform(bedwarsPlayer -> bedwarsPlayer.sendTitle(null, AllStrings.YOUR_BED_BROKEN.get()));
        teamManager.performAll(bedwarsPlayer -> {
            bedwarsPlayer.playSound(Sound.ENDERDRAGON_GROWL, 9.0f, 1.25f);
            bedwarsPlayer.sendMessage(AllStrings.BED_BROKEN_BY.get() + brokenBy.getBedwarsTeam().getColorCode() + brokenBy.getName());
        });
    }
}
