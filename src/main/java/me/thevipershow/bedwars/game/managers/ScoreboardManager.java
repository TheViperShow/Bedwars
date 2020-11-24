package me.thevipershow.bedwars.game.managers;

import java.util.HashMap;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.scoreboards.BedwarsScoreboardHandler;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import org.bukkit.scheduler.BukkitTask;

public final class ScoreboardManager {

    private final ActiveGame activeGame;

    public ScoreboardManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
        this.scoreboardHandler = new BedwarsScoreboardHandler(activeGame);
    }

    private BukkitTask scoreboardUpdateTask = null;

    private final HashMap<BedwarsPlayer, Scoreboard> scoreboardMap = new HashMap<>();

    private final ScoreboardHandler scoreboardHandler;

    public final BukkitTask getScoreboardUpdateTask() {
        return scoreboardUpdateTask;
    }

    public final void setScoreboardUpdateTask(BukkitTask scoreboardUpdateTask) {
        this.scoreboardUpdateTask = scoreboardUpdateTask;
    }

    public final ScoreboardHandler getScoreboardHandler() {
        return scoreboardHandler;
    }

    public final HashMap<BedwarsPlayer, Scoreboard> getScoreboardMap() {
        return scoreboardMap;
    }

    public final void deactivateAllScoreboards() {
        for (Scoreboard scoreboard : scoreboardMap.values()) {
            scoreboard.deactivate();
        }
    }

    public final void activateAll() {
        for (Scoreboard value : scoreboardMap.values()) {
            value.activate();
        }
    }

    public final void assignScoreboards() {
        for (final Map.Entry<BedwarsTeam, ? extends TeamData<?>> entry : activeGame.getTeamManager().getDataMap().entrySet()) {
            entry.getValue().perform(bedwarsPlayer -> {
                final Scoreboard scoreboard = ScoreboardLib.createScoreboard(bedwarsPlayer.getPlayer());
                scoreboard.setHandler(scoreboardHandler);
                scoreboard.setUpdateInterval(20L);
                scoreboardMap.put(bedwarsPlayer, scoreboard);
            });
        }
    }
}
