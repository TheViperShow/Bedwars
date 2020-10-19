package me.thevipershow.bedwars.placeholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.thevipershow.bedwars.LoggerUtils;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ExperienceManager;
import me.thevipershow.bedwars.game.GameManager;
import me.thevipershow.bedwars.game.Pair;
import me.thevipershow.bedwars.storage.sql.tables.RankTableUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public final class BedwarsExpansion extends PlaceholderExpansion {

    private final GameManager gameManager;

    private final static String AUTHOR = "TheViperShow", IDENTIFIER = "bedwars";
    private final String version;
    private BukkitTask cacheExpTask = null;

    private final HashMap<UUID, Pair<Long, Character>> cachedTeamColors = new HashMap<>();
    private final HashMap<UUID, Integer> cachedExp = new HashMap<>();

    public BedwarsExpansion(final GameManager gameManager) {
        this.gameManager = gameManager;
        this.version = gameManager.getPlugin().getDescription().getVersion();

        startCacheExpTask();
    }

    private void startCacheExpTask() {
        if (this.cacheExpTask == null) {
            LoggerUtils.logColor(gameManager.getPlugin().getLogger(), "&eCaching player EXP into local data. . .");
            this.cacheExpTask = gameManager.getPlugin().getServer().getScheduler().runTaskTimer(gameManager.getPlugin(), () -> RankTableUtils.cacheIntoMap(this.cachedExp, gameManager.getPlugin()), 1L, 20L * 45L);
        }
    }

    public final void stopCacheExpTask() {
        if (this.cacheExpTask != null) {
            this.cacheExpTask.cancel();
        }
    }

    @Override
    @NotNull
    public final String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    @NotNull
    public final String getAuthor() {
        return AUTHOR;
    }

    @Override
    @NotNull
    public final String getVersion() {
        return this.version;
    }

    private String getPlayerTeam(final Player player) {
        if (cachedTeamColors.containsKey(player.getUniqueId())) {
            final Pair<Long, Character> character = cachedTeamColors.get(player.getUniqueId());
            if (System.currentTimeMillis() - character.getA() <= 15_000) {
                return character.getB().toString();
            }
        } else if (player.isOnline()) {
            final Player p = player.getPlayer();
            for (final ActiveGame activeGame : gameManager.getWorldsManager().getActiveGameList()) {
                if (!activeGame.getAssociatedWorld().equals(p.getWorld())) {
                    continue;
                }

                for (final Map.Entry<BedwarsTeam, List<Player>> entry : activeGame.getAssignedTeams().entrySet()) {
                    for (final Player player1 : entry.getValue()) {
                        if (player1.getUniqueId().equals(player.getUniqueId())) {
                            final Character pChar = entry.getKey().getColorCode();
                            cachedTeamColors.put(player.getUniqueId(), new Pair<>(System.currentTimeMillis(), pChar));
                            return pChar.toString();
                        }
                    }
                }
            }
        }
        return "";
    }

    @Override
    public final boolean canRegister() {
        return true;
    }

    private String getPlayerExpAbs(final Player player) {
        final Integer i = this.cachedExp.get(player.getUniqueId());
        if (i == null) {
            return "0";
        } else {
            return i.toString();
        }
    }

    private String getPlayerCurrentExpRel(final Player player) {
        final Integer i = this.cachedExp.get(player.getUniqueId());
        final int playerLevel = ExperienceManager.findLevelFromExp(i);
        final int playerLevelMinExp = ExperienceManager.requiredExpMap.get(playerLevel);
        final int playerNextLevelMinExp = ExperienceManager.requiredExpMap.get(playerLevel + 1);

        final int expForNextLevel = playerNextLevelMinExp - playerLevelMinExp;
        final int currentLevelExp = i - playerLevelMinExp;

        return Integer.toString(currentLevelExp);
    }

    private String getPlayerCurrentLevelExpRel(final Player player) {
        final Integer i = this.cachedExp.get(player.getUniqueId());
        final int playerLevel = ExperienceManager.findLevelFromExp(i);
        final int playerLevelMinExp = ExperienceManager.requiredExpMap.get(playerLevel);
        final int playerNextLevelMinExp = ExperienceManager.requiredExpMap.get(playerLevel + 1);

        final int expForNextLevel = playerNextLevelMinExp - playerLevelMinExp;

        return Integer.toString(expForNextLevel);
    }


    private String getPlayerLevel(final Player player) {
        final Integer i = this.cachedExp.get(player.getUniqueId());
        if (i == null) {
            return "0";
        } else {
            return Integer.toString(ExperienceManager.findLevelFromExp(i));
        }
    }

    @Override
    public final String onPlaceholderRequest(final Player player, final @NotNull String identifier) {

        if (player == null) {
            return "";
        }

        switch (identifier) {
            case "level":
                return getPlayerLevel(player);
            case "exp_abs":
                return getPlayerExpAbs(player);
            case "exp_current_rel":
                return getPlayerCurrentExpRel(player);
            case "exp_required_rel":
                return getPlayerCurrentLevelExpRel(player);
            case "team_colour":
                return getPlayerTeam(player);
        }

        return null;
    }
}
