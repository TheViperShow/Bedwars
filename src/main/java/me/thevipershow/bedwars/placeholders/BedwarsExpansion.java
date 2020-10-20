package me.thevipershow.bedwars.placeholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.thevipershow.bedwars.LoggerUtils;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ExperienceManager;
import me.thevipershow.bedwars.game.GameManager;
import me.thevipershow.bedwars.game.Pair;
import me.thevipershow.bedwars.storage.sql.tables.GlobalStatsTableUtils;
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
    private BukkitTask cacheExpTask = null, cacheWinsTask = null, cacheKillsTask = null;

    private final HashMap<UUID, Pair<Long, Character>> cachedTeamColors = new HashMap<>();
    private final HashMap<UUID, Integer> cachedExp = new HashMap<>();
    private final HashMap<UUID, GlobalStatsTableUtils.Wins> cachedWins = new HashMap<>();
    private final HashMap<UUID, GlobalStatsTableUtils.Kills> cachedKills = new HashMap<>();
    private final HashMap<UUID, GlobalStatsTableUtils.Kills> cachedFinalKills = new HashMap<>();

    public BedwarsExpansion(final GameManager gameManager) {
        this.gameManager = gameManager;
        this.version = gameManager.getPlugin().getDescription().getVersion();

        startCacheExpTask();
        startCacheWinsTask();
        startCacheKillsTask();
    }

    private void startCacheExpTask() {
        if (this.cacheExpTask == null) {
            LoggerUtils.logColor(gameManager.getPlugin().getLogger(), "&eCaching player EXP into local data. . .");
            this.cacheExpTask = gameManager.getPlugin().getServer().getScheduler().runTaskTimer(gameManager.getPlugin(), () -> RankTableUtils.cacheIntoMap(this.cachedExp, gameManager.getPlugin()), 1L, 20L * 30L);
        }
    }

    private void startCacheWinsTask() {
        if (this.cacheWinsTask == null) {
            LoggerUtils.logColor(gameManager.getPlugin().getLogger(), "&eCaching player win into local data. . .");
            this.cacheWinsTask = gameManager.getPlugin().getServer().getScheduler().runTaskTimer(gameManager.getPlugin(), () -> GlobalStatsTableUtils.cacheWinsIntoMap(this.cachedWins, gameManager.getPlugin()), 1L, 20L * 60L);
        }
    }

    private void startCacheKillsTask() {
        if (this.cacheKillsTask == null) {
            LoggerUtils.logColor(gameManager.getPlugin().getLogger(), "&eCaching player kills into local data. . .");
            this.cacheKillsTask = gameManager.getPlugin().getServer().getScheduler().runTaskTimer(gameManager.getPlugin(), () -> {
                GlobalStatsTableUtils.cacheKillsIntoMap(this.cachedKills, gameManager.getPlugin(),false);
                GlobalStatsTableUtils.cacheKillsIntoMap(this.cachedFinalKills, gameManager.getPlugin(), true);
            }, 1L, 20L * 60L);
        }
    }

    public final void stopCacheExpTask() {
        if (this.cacheExpTask != null) {
            this.cacheExpTask.cancel();
        }
    }

    public final void stopCacheWinsTask() {
        if (this.cacheWinsTask != null) {
            this.cacheWinsTask.cancel();
        }
    }

    public final void stopCacheKillsTask() {
        if (this.cacheKillsTask != null) {
            this.cacheKillsTask.cancel();
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
        if (cachedTeamColors.containsKey(player.getUniqueId()) && System.currentTimeMillis() - cachedTeamColors.get(player.getUniqueId()).getA() <= 10_000) {
            final Pair<Long, Character> character = cachedTeamColors.get(player.getUniqueId());
                return "&" + character.getB().toString();
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
                            return "&" + pChar.toString();
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
        if (i == null) {
            return "0";
        }
        final int playerLevel = ExperienceManager.findLevelFromExp(i);
        final int playerLevelMinExp = ExperienceManager.requiredExpMap.get(playerLevel);
        final int playerNextLevelMinExp = ExperienceManager.requiredExpMap.get(playerLevel + 1);

        final int expForNextLevel = playerNextLevelMinExp - playerLevelMinExp;
        final int currentLevelExp = i - playerLevelMinExp;

        return Integer.toString(currentLevelExp);
    }

    private String getPlayerCurrentLevelExpRel(final Player player) {
        final Integer i = this.cachedExp.get(player.getUniqueId());
        if (i == null) {
            return Integer.toString(ExperienceManager.FIRST_LVL);
        }
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

    private String getKills(final Gamemode gamemode, final Player player, final boolean finalKill) {
        final Integer i = !finalKill ? this.cachedKills.get(player.getUniqueId()).getKills(gamemode) : this.cachedFinalKills.get(player.getUniqueId()).getKills(gamemode);
        if (i == null) {
            return "0";
        }
        return i.toString();
    }

    private String getWins(final Gamemode gamemode, final Player player) {
        if (!cachedWins.containsKey(player.getUniqueId())) {
            return "0";
        }
        final int i = this.cachedWins.get(player.getUniqueId()).getWin(gamemode);
        return Integer.toString(i);
    }

    private String getTotalWins(final Player player) {
        final GlobalStatsTableUtils.Wins wins = this.cachedWins.get(player.getUniqueId());
        if (wins == null) {
            return "0";
        }
        return Integer.toString(wins.getSoloWins() + wins.getDuoWins() + wins.getQuadWins());
    }

    private String getTotalKills(final Player player, final boolean finalKills) {
        final GlobalStatsTableUtils.Kills kills = finalKills ? this.cachedFinalKills.get(player.getUniqueId()) : this.cachedKills.get(player.getUniqueId());
        if (kills == null) {
            return "0";
        }
        return Integer.toString(kills.getSoloKills() + kills.getDuoKills() + kills.getQuadKills());
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
            case "solo_kills":
                return getKills(Gamemode.SOLO, player, true);
            case "duo_kills":
                return getKills(Gamemode.DUO, player, true);
            case "quad_kills":
                return getKills(Gamemode.QUAD, player, true);
            case "total_kills":
                return getTotalKills(player, true);
            case "solo_fkills":
                return getKills(Gamemode.SOLO, player, false);
            case "duo_fkills":
                return getKills(Gamemode.DUO, player, false);
            case "quad_fkills":
                return getKills(Gamemode.QUAD, player, false);
            case "total_fkills":
                return getTotalKills(player, false);
            case "solo_wins":
                return getWins(Gamemode.SOLO, player);
            case "duo_wins":
                return getWins(Gamemode.DUO, player);
            case "quad_wins":
                return getWins(Gamemode.QUAD, player);
            case "total_wins":
                return getTotalWins(player);
        }

        return null;
    }
}
