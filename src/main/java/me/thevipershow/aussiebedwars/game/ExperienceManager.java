package me.thevipershow.aussiebedwars.game;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import me.thevipershow.aussiebedwars.events.BedwarsLevelUpEvent;
import me.thevipershow.aussiebedwars.storage.sql.queue.RankTableUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public final class ExperienceManager {

    private final ActiveGame activeGame;
    public static final Map<Integer, Integer> requiredExpMap = new LinkedHashMap<>();

    public ExperienceManager(final ActiveGame activeGame) {
        this.activeGame = activeGame;

        for (int j = 0; j <= 10; j++) {
            if (j == 0) {
                requiredExpMap.put(1, FIRST_LVL);
                requiredExpMap.put(2, SECOND_LVL);
                requiredExpMap.put(3, THIRD_LVL);
                requiredExpMap.put(4, FOURTH_LVL);
            } else {
                requiredExpMap.put((100 * j) + 1, requiredExpMap.get(100 * j) + FIRST_LVL);
                requiredExpMap.put((100 * j) + 2, requiredExpMap.get(100 * j) + SECOND_LVL);
                requiredExpMap.put((100 * j) + 3, requiredExpMap.get(100 * j) + THIRD_LVL);
                requiredExpMap.put((100 * j) + 4, requiredExpMap.get(100 * j) + FOURTH_LVL);
            }
            for (int i = 5; i <= 100; i++) {
                requiredExpMap.put(i + (j * 100), 5000 + requiredExpMap.get((i + (j * 100)) - 1));
            }
        }

    }

    public final static long PLAY_REWARD_DELAY = 60L * 20L;
    public final static int PLAY_REWARD_EXP = 25;
    public final static int FIRST_LVL = 500,
            SECOND_LVL = 1500,
            THIRD_LVL = 3500,
            FOURTH_LVL = 7000;

    private BukkitTask playtimeExperienceTask = null;

    public static Optional<Integer> hasLevelledUp(final int currentExp, final int givenExp) {

        final int currentLevel = findLevelFromExp(currentExp);
        final int increasedLevel = findLevelFromExp(currentExp + givenExp);
        if (currentLevel < increasedLevel) {
            return Optional.of(increasedLevel);
        }

        return Optional.empty();
    }

    public static int findLevelFromExp(final int exp) {
        if (exp < 500) {
            return 0;
        }

        final Iterator<Map.Entry<Integer, Integer>> i = requiredExpMap.entrySet().iterator();
        Map.Entry<Integer, Integer> last = null;
        while (i.hasNext()) {
            if (last == null) {
                last = i.next();
            } else {
                final Map.Entry<Integer, Integer> v = i.next();
                if (exp > v.getValue()) {
                    last = v;
                } else {
                    return last.getKey();
                }
            }
        }
        return last.getValue();
    }

    public static void rewardPlayer(final int experience, final Player p, final ActiveGame activeGame) {
        if (p.isOnline() && !activeGame.isOutOfGame(p)) {
            RankTableUtils.rewardPlayerExp(p, experience, activeGame.getPlugin()); // rewarding him exp
            RankTableUtils.getPlayerExp(p.getUniqueId(), activeGame.getPlugin()).thenAccept(pExp -> {
                if (pExp != 0) {
                    final Optional<Integer> hasLevelledUp = hasLevelledUp(pExp, experience);
                    hasLevelledUp.ifPresent(newLevel -> {
                        final BedwarsLevelUpEvent e = new BedwarsLevelUpEvent(p, (newLevel - 1), newLevel, activeGame);
                        activeGame.getPlugin().getServer().getPluginManager().callEvent(e);
                    });
                }
            });
        }
    }

    private void rewardAllPlayingPlayers(final int experience) {
        activeGame.getAssignedTeams().values().stream().flatMap(Collection::stream).forEach(p -> rewardPlayer(experience, p, this.activeGame));
    }

    public final void startRewardTask() {
        if (this.playtimeExperienceTask != null) {
            return;
        }
        this.playtimeExperienceTask = activeGame.getPlugin().getServer().getScheduler().runTaskTimer(activeGame.getPlugin(),
                () -> rewardAllPlayingPlayers(PLAY_REWARD_EXP), PLAY_REWARD_DELAY, PLAY_REWARD_DELAY);
    }

    public final void stopRewardTask() {
        if (this.playtimeExperienceTask == null) {
            return;
        }
        this.playtimeExperienceTask.cancel();
    }

}
