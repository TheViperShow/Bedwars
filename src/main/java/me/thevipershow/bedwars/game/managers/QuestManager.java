package me.thevipershow.bedwars.game.managers;

import java.util.concurrent.CompletableFuture;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.game.managers.ExperienceManager;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.storage.sql.tables.QuestsTableUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class QuestManager {

    private final ExperienceManager experienceManager;
    private final Plugin plugin;

    public QuestManager(final ExperienceManager experienceManager) {
        this.experienceManager = experienceManager;
        this.plugin = experienceManager.getActiveGame().getPlugin();
    }

    public final void rewardAllAtEndGame() {
        experienceManager.getActiveGame().getTeamManager().performAll(this::gamePlayedReward);
    }

    private void dailyFirstGameMessage(Player player) {
        player.sendMessage(AllStrings.DAILY_FIRST_GAME_MESSAGE.get());
    }

    private void dailyGamesPlayedMessage(Player player) {
        player.sendMessage(AllStrings.DAILY_GAMES_PLAYER_MESSAGE.get());
    }

    private void brokenBedsMessage(Player player) {
        player.sendMessage(AllStrings.WEEKLY_BROKEN_BEDS_MESSAGE.get());
    }

    public final void winDailyFirstGame(BedwarsPlayer player) {
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException("This BedwarsPlayer was null.");
        }
        Player p = player.getPlayer();
        final CompletableFuture<Boolean> dailyFirstWin = QuestsTableUtils.getDailyFirstWin(plugin, p);
        dailyFirstWin.thenAccept(bool -> {
            if (bool == null || !bool) {
                ExperienceManager.rewardPlayer(250, player, experienceManager.getActiveGame());
                QuestsTableUtils.setDailyFirstWin(plugin, p);
                dailyFirstGameMessage(p);
            }
        });
    }

    public final void gamePlayedReward(BedwarsPlayer player) {
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException("This BedwarsPlayer was null.");
        }
        Player p = player.getPlayer();
        final CompletableFuture<Integer> gamesPlayed = QuestsTableUtils.getDailyGamesPlayed(plugin, p);
        gamesPlayed.thenAccept(i -> {
            if (i != null) {
                if (i == 1) {
                    dailyGamesPlayedMessage(p);
                    ExperienceManager.rewardPlayer(250, player, experienceManager.getActiveGame());
                }
            }
            QuestsTableUtils.increaseGamesPlayed(plugin, p);
        });
    }

    public final void breakBedReward(@NotNull BedwarsPlayer player) {
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException("This BedwarsPlayer was null.");
        }
        Player p = player.getPlayer();
        final CompletableFuture<Integer> brokenBeds = QuestsTableUtils.getBedsBroken(player.getUniqueId(), plugin);
        brokenBeds.thenAccept(i -> {
            if (i == null) {
                if (i == 24) {
                    brokenBedsMessage(p);
                    ExperienceManager.rewardPlayer(5000, player, experienceManager.getActiveGame());
                }
            }

            QuestsTableUtils.increaseBrokenBeds(plugin, p);
        });
    }

}
