package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.events.BedwarsPlayerQuitEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.objects.BedwarsPlayer;
import me.thevipershow.bedwars.game.objects.TeamManager;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class BedwarsPlayerQuitUnregisterableListener extends UnregisterableListener {
    public BedwarsPlayerQuitUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    private void announcePlayerQuit(BedwarsPlayer bedwarsPlayer) {
        TeamManager<?> teamManager = activeGame.getTeamManager();
        BedwarsTeam quitTeam = bedwarsPlayer.getBedwarsTeam();
        final String message = GameUtils.color("&" + quitTeam.getColorCode() + bedwarsPlayer.getName() + " &7has quit this game.");
        teamManager.performAll(bp -> bp.sendMessage(message));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBedwarsPlayerQuit(BedwarsPlayerQuitEvent event) {
        ActiveGame activeGame = event.getActiveGame();
        if (!activeGame.equals(event.getActiveGame())) {
            return;
        }

        BedwarsPlayer quit = event.getBedwarsPlayer();
        announcePlayerQuit(quit);
    }
}
