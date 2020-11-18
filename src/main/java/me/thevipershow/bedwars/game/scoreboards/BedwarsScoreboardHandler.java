package me.thevipershow.bedwars.game.scoreboards;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import me.thevipershow.bedwars.game.deathmatch.AbstractDeathmatch;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.managers.TeamManager;
import me.thevipershow.bedwars.game.spawners.ActiveSpawner;
import me.thevipershow.bedwars.game.GameUtils;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import static me.thevipershow.bedwars.game.GameUtils.color;

public final class BedwarsScoreboardHandler implements ScoreboardHandler {

    private final ActiveGame activeGame;

    public BedwarsScoreboardHandler(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @Override
    public final String getTitle(final Player player) {
        return Bedwars.PREFIX;
    }

    @Override
    public final List<Entry> getEntries(final Player player) {
        final EntryBuilder builder = new EntryBuilder();

        BedwarsPlayer bedwarsPlayer = activeGame.getPlayerMapper().get(player);

        builder.next("  " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
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

        AbstractDeathmatch deathmatch = activeGame.getAbstractDeathmatch();

        if (deathmatch.isRunning()) {
            builder.next(GameUtils.generateDeathmatch(deathmatch));
            builder.next(GameUtils.generateDragons(deathmatch));
        }

        TeamManager<?> teamManager = activeGame.getTeamManager();
        Map<BedwarsTeam, ? extends TeamData<?>> managerDataMap = teamManager.getDataMap();
        for (BedwarsTeam team : BedwarsTeam.values()) {
            if (managerDataMap.containsKey(team)) {
                String status = managerDataMap.get(team).getStatusCharacter();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(AllStrings.TEAM_SCOREBOARD.get()).append(team.getColorCode()).append(ChatColor.BOLD).append(team.name()).append(" ");
                if (bedwarsPlayer == null || bedwarsPlayer.getBedwarsTeam() != team) {
                    stringBuilder.append(status);
                } else {
                    stringBuilder.append(status);
                    stringBuilder.append(ChatColor.GRAY + " YOU");
                }
                builder.next(color(stringBuilder.toString()));
            } else {
                builder.next(color(AllStrings.TEAM_SCOREBOARD.get() + team.getColorCode() + ChatColor.BOLD + team.name() + " " + AllStrings.GRAPHIC_CROSS.get()));
            }
        }

        builder.blank();
        builder.next(" " + ChatColor.YELLOW + AllStrings.SERVER_BRAND.get());
        return builder.build();
    }
}
