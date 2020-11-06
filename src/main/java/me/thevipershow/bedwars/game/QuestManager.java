package me.thevipershow.bedwars.game;

import java.util.concurrent.CompletableFuture;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.storage.sql.tables.QuestsTableUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class QuestManager {

    private final ExperienceManager experienceManager;
    private final Plugin plugin;

    public QuestManager(final ExperienceManager experienceManager) {
        this.experienceManager = experienceManager;
        this.plugin = experienceManager.getActiveGame().getPlugin();
    }

    private void dailyFirstGameMessage(final Player player) {
        player.sendMessage(AllStrings.DAILY_FIRST_GAME_MESSAGE.get());
    }

    private void dailyGamesPlayedMessage(final Player player) {
        player.sendMessage(AllStrings.DAILY_GAMES_PLAYER_MESSAGE.get());
    }

    private void brokenBedsMessage(final Player player) {
        player.sendMessage(AllStrings.WEEKLY_BROKEN_BEDS_MESSAGE.get());
    }

    public final void winDailyFirstGame(final Player player) {
        final CompletableFuture<Boolean> dailyFirstWin = QuestsTableUtils.getDailyFirstWin(plugin, player);
        dailyFirstWin.thenAccept(bool -> {
            if (bool == null || !bool) {
                ExperienceManager.rewardPlayer(250, player, experienceManager.getActiveGame());
                QuestsTableUtils.setDailyFirstWin(plugin, player);
                dailyFirstGameMessage(player);
            }
        });
    }

    public final void gamePlayedReward(final Player player) {
        final CompletableFuture<Integer> gamesPlayed = QuestsTableUtils.getDailyGamesPlayed(plugin, player);
        gamesPlayed.thenAccept(i -> {
            if (i != null) {
                if (i == 1) {
                    dailyGamesPlayedMessage(player);
                    ExperienceManager.rewardPlayer(250, player, experienceManager.getActiveGame());
                }
            }
            QuestsTableUtils.increaseGamesPlayed(plugin, player);
        });
    }

    public final void breakBedReward(final Player player) {
        final CompletableFuture<Integer> brokenBeds = QuestsTableUtils.getBedsBroken(player.getUniqueId(), plugin);
        brokenBeds.thenAccept(i -> {
            if (i == null) {
                if (i == 24) {
                    brokenBedsMessage(player);
                    ExperienceManager.rewardPlayer(5000, player, experienceManager.getActiveGame());
                }
            }

            QuestsTableUtils.increaseBrokenBeds(plugin, player);
        });
    }

}