package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.events.TeamBedDestroyEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import me.thevipershow.bedwars.game.managers.TeamManager;
import me.thevipershow.bedwars.game.data.game.enums.TeamStatus;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;

public final class BedDestroyUnregisterableListener extends UnregisterableListener {
    public BedDestroyUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onTeamBedDestroy(TeamBedDestroyEvent event) {
        BedwarsTeam brokenTeam = event.getDestroyedTeam();
        if (!event.getActiveGame().equals(activeGame)) {
            return;
        }

        BedwarsPlayer brokenBy = event.getDestroyer();
        BedwarsTeam brokenByTeam = brokenBy.getBedwarsTeam();

        TeamManager<?> teamManager = activeGame.getTeamManager();
        TeamData<?> data = teamManager.dataOfTeam(brokenTeam);
        data.setStatus(TeamStatus.BED_BROKEN);

        data.perform(bedwarsPlayer -> bedwarsPlayer.sendTitle(null, AllStrings.YOUR_BED_BROKEN.get()));
        teamManager.performAll(bedwarsPlayer -> {
            bedwarsPlayer.playSound(Sound.ENDERDRAGON_GROWL, 10.0f, 1.0f);
            bedwarsPlayer.sendMessage(GameUtils.color("&" + brokenTeam.getColorCode() + brokenTeam.name() + AllStrings.BED_BROKEN_BY.get() + brokenByTeam.getColorCode() + brokenBy.getName()));
        });
    }
}
