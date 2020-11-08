package me.thevipershow.bedwars.game.objects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveSpawner;
import me.thevipershow.bedwars.game.GameUtils;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public final class ScoreboardManager {

    private final ActiveGame activeGame;

    public ScoreboardManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private BukkitTask scoreboardUpdateTask = null;

    private final HashMap<BedwarsPlayer, Scoreboard> scoreboardMap = new HashMap<>();

    private final ScoreboardHandler scoreboardHandler = new ScoreboardHandler() {

        @Override
        public final String getTitle(final Player player) {
            return Bedwars.PREFIX;
        }

        @Override
        public final List<Entry> getEntries(final Player player) {
            final EntryBuilder builder = new EntryBuilder();
            builder.next("   " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            builder.blank();

            ActiveSpawner emeraldSample = activeGame.getActiveSpawnersManager().getEmeraldSampleSpawner();
            ActiveSpawner diamondSample = activeGame.getActiveSpawnersManager().getDiamondSampleSpawner();
            if (emeraldSample != null && diamondSample != null) {
                final String diamondText = GameUtils.generateScoreboardMissingTimeSpawners(diamondSample);
                final String emeraldText = GameUtils.generateScoreboardMissingTimeSpawners(emeraldSample);
                builder.next(diamondText);
                builder.next(emeraldText);
                builder.blank();
            }

            //if (abstractDeathmatch.isRunning()) {
            //    builder.next(GameUtils.generateDeathmatch(abstractDeathmatch));
            //    builder.next(GameUtils.generateDragons(abstractDeathmatch));
            //}
            // TODO: Reimplement^

            for (BedwarsTeam team : activeGame.getTeamManager().getDataMap().keySet()) {
                final String status = activeGame.getTeamManager().getDataMap().get(team).getStatusCharacter();
                final String append = AllStrings.TEAM_SCOREBOARD.get() + team.getColorCode() + "§l" + team.name() + " " + status;
                builder.next(append);
            }

            builder.blank();
            builder.next(" §e" + AllStrings.SERVER_BRAND.get());
            return builder.build();
        }

    };

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

    public final void activateAll() {
        for (Scoreboard value : scoreboardMap.values()) {
            value.activate();
        }
    }

    public final void assignScoreboards() {
        for (Map.Entry<BedwarsTeam, ? extends TeamData<?>> entry : activeGame.getTeamManager().getDataMap().entrySet()) {
            entry.getValue().perform(bedwarsPlayer -> {
                final Scoreboard scoreboard = ScoreboardLib.createScoreboard(bedwarsPlayer.getPlayer());
                scoreboard.setHandler(scoreboardHandler);
                scoreboard.setUpdateInterval(5 * 20);
                scoreboardMap.put(bedwarsPlayer, scoreboard);
            });
        }
    }
}
