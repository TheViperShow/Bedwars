package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.events.TeamWinEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.objects.TeamData;
import me.thevipershow.bedwars.game.objects.TeamManager;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

public final class TeamWinUnregisterableListener extends UnregisterableListener {

    private final static long STOP_AFTER_TICKS = 20L * 10; // 10 seconds

    public TeamWinUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onTeamWin(TeamWinEvent event) {
        BedwarsTeam winnerTeam = event.getBedwarsTeam(); // the team that has just won this game.
        ActiveGame activeGame = event.getActiveGame(); // getting the game.

        if (!activeGame.equals(super.activeGame)) {
            return; // this should never happen, but check is for safety.
        }

        announceEndOfGame();
        announceWinners(winnerTeam);
        activeGame.getKillTracker().announceTopThreeScores();

        stopGameLater();
    }

    /**
     * Announces that the game has ended to everyone.
     */
    private void announceEndOfGame() {
        TeamManager<?> teamManager = activeGame.getTeamManager();
        teamManager.performAll(bp -> bp.sendMessage(ChatColor.GRAY + "This game has finished!"));
    }

    /**
     * Announces the winners of this game to everyone.
     * @param winnerTeam The winning team.
     */
    private void announceWinners(BedwarsTeam winnerTeam) {
        TeamManager<?> teamManager = activeGame.getTeamManager();
        Gamemode gamemode = activeGame.getBedwarsGame().getGamemode();
        final String winnersEnumeration = generateWinnersString(winnerTeam);
        teamManager.performAll(bp -> {
            if (gamemode == Gamemode.SOLO) {
                bp.sendMessage(GameUtils.color(ChatColor.GRAY + "The winner of this game is:"));
            } else {
                bp.sendMessage(GameUtils.color(ChatColor.GRAY + "The winners of this game are:"));
            }
            bp.sendMessage(winnersEnumeration);
        });
    }

    private String generateWinnersString(BedwarsTeam winnerTeam) {
        final StringBuilder builder = new StringBuilder();
        TeamManager<?> teamManager = activeGame.getTeamManager();
        TeamData<?> dataOfWinnerTeam = teamManager.dataOfTeam(winnerTeam);
        if (dataOfWinnerTeam != null) {
            final char colorCode = winnerTeam.getColorCode();
            dataOfWinnerTeam.perform(bp -> builder.append("  &7- &l&").append(colorCode).append(bp.getName()).append('\n'));
        }
        return GameUtils.color(builder.toString());
    }

    private void stopGameLater() {
        Plugin plugin = super.activeGame.getPlugin();
        plugin.getServer().getScheduler().runTaskLater(plugin, activeGame::stop, STOP_AFTER_TICKS);
    }
}
