package me.thevipershow.bedwars.game.managers;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import me.thevipershow.bedwars.events.BedwarsLevelUpEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.storage.sql.tables.RankTableUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public final class ExperienceManager {


    private final ActiveGame activeGame;
    private BukkitTask playtimeExperienceTask = null;

    public static final Map<Integer, Integer> requiredExpMap = new LinkedHashMap<>();
    public final static long PLAY_REWARD_DELAY = 60L * 20L;
    public final static int PLAY_REWARD_EXP = 25;
    public final static int FIRST_LVL = 500, SECOND_LVL = 1500, THIRD_LVL = 3500, FOURTH_LVL = 7000;


    static {
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

    public ExperienceManager(@NotNull ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    /**
     * This method determines if a player with a certain experience amount, will
     * or will not level up after adding another amount of experience points.
     * The optional will be empty when adding experience doesn't move the player
     * to a new level.
     *
     * @param currentExp The player's current total experience points.
     * @param givenExp   The experience points to add.
     * @return Level optional.
     */
    @NotNull
    public static Optional<Integer> hasLevelledUp(final int currentExp, final int givenExp) {

        final int currentLevel = findLevelFromExp(currentExp);
        final int increasedLevel = findLevelFromExp(currentExp + givenExp);
        if (currentLevel < increasedLevel) {
            return Optional.of(increasedLevel);
        }

        return Optional.empty();
    }

    /**
     * This method finds a level based on the experience of the player.
     * It could fail upon invalid values, such as negative experience, or values that are too high.
     *
     * @param exp The total experience.
     * @return The level mapped to this experience interval.
     */
    public static int findLevelFromExp(int exp) {
        if (exp < FIRST_LVL) {
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

    /**
     * This method allows you to reward a single BedwarsPlayer that is currently playing in the ActiveGame.
     * Increasing this player's experience will may trigger a {@link BedwarsLevelUpEvent} if he had
     * enough experience to reach the new level. If the {@link BedwarsLevelUpEvent} associated with this
     * increase becomes cancelled, the player will no longer receive the experience that would've otherwise
     * been granted by the call of this method.
     *
     * @param experience The experience that this player will receive after having called the method.
     * @param p          The {@link BedwarsPlayer} who will receive the experience.
     * @param activeGame The {@link ActiveGame} that this player is playing in.
     */
    public static void rewardPlayer(int experience, @NotNull BedwarsPlayer p, @NotNull ActiveGame activeGame) {
        if (p == null || !p.isOnline()) {
            throw new IllegalArgumentException("BedwarsPlayer could not be rewarded as he was NULL");
        }
        if (activeGame == null) {
            throw new IllegalArgumentException("BedwarsPlayer could not be rewarded as the ActiveGame was NULL");
        }

        Plugin plugin = activeGame.getPlugin();

        RankTableUtils.getPlayerExp(p.getUniqueId(), plugin)
                .thenAccept(e -> {
                    final int sum = e + experience;
                    if (sum != 0) {
                        Optional<Integer> hasLevelledUp = hasLevelledUp(sum, experience);
                        hasLevelledUp.ifPresent(newLevel -> {
                            BedwarsLevelUpEvent event = new BedwarsLevelUpEvent(p, newLevel - 1, newLevel, activeGame);
                            plugin.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                RankTableUtils.rewardPlayerExp(p.getPlayer(), experience, plugin);
                            }
                        });
                    }
                });
    }

    /**
     * This method allows you to reward all players playing in the current game.
     * Each player will receieve the same amount of experience and eventually
     * trigger other events such as {@link BedwarsLevelUpEvent}.
     *
     * @param experience The experience that all players will recieve.
     */
    private void rewardAllPlayingPlayers(int experience) {
        activeGame.getTeamManager().performAll(bp -> rewardPlayer(experience, bp, this.activeGame));
    }

    /**
     * Start a rewarding task.
     * This task will automatically reward all players for playing.
     */
    public final void startRewardTask() {
        if (this.playtimeExperienceTask != null) {
            return;
        }
        this.playtimeExperienceTask = activeGame.getPlugin().getServer().getScheduler().runTaskTimer(activeGame.getPlugin(),
                () -> rewardAllPlayingPlayers(PLAY_REWARD_EXP), PLAY_REWARD_DELAY, PLAY_REWARD_DELAY);
    }

    /**
     * Stops the rewarding task if it has started before.
     */
    public final void stopRewardTask() {
        if (this.playtimeExperienceTask == null) {
            return;
        }
        this.playtimeExperienceTask.cancel();
    }

    /**
     * Get the {@link ActiveGame} that this object
     * has been instantiated from.
     *
     * @return The ActiveGame
     */
    public final ActiveGame getActiveGame() {
        return activeGame;
    }
}
