package me.thevipershow.bedwars.placeholders;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.thevipershow.bedwars.LoggerUtils;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ExperienceManager;
import me.thevipershow.bedwars.game.GameManager;
import me.thevipershow.bedwars.game.Pair;
import me.thevipershow.bedwars.game.objects.BedwarsPlayer;
import me.thevipershow.bedwars.game.objects.TeamData;
import me.thevipershow.bedwars.storage.sql.tables.GlobalStatsTableUtils;
import me.thevipershow.bedwars.storage.sql.tables.RankTableUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public final class BedwarsExpansion extends PlaceholderExpansion {

    private final GameManager gameManager;

    private final static String AUTHOR = "TheViperShow", IDENTIFIER = "bedwars";
    private final String version;
    private BukkitTask cacheExpTask = null, cacheWinsTask = null, cacheKillsTask = null;

    private final HashMap<UUID, Pair<Long, Character>> cachedTeamColors = new HashMap<>();
    private final LinkedHashMap<UUID, Integer> cachedExp = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, GlobalStatsTableUtils.Wins> cachedWins = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, GlobalStatsTableUtils.Kills> cachedKills = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, GlobalStatsTableUtils.Kills> cachedFinalKills = new LinkedHashMap<>();

    private final EnumMap<Gamemode, LinkedList<Pair<UUID, Integer>>> topWins = new EnumMap<>(Gamemode.class);
    private final EnumMap<Gamemode, LinkedList<Pair<UUID, Integer>>> topKills = new EnumMap<>(Gamemode.class);
    private final EnumMap<Gamemode, LinkedList<Pair<UUID, Integer>>> topFinalKills = new EnumMap<>(Gamemode.class);
    private final LinkedList<Pair<UUID, Integer>> topTotalWins = new LinkedList<>();
    private final LinkedList<Pair<UUID, Integer>> topExp = new LinkedList<>();

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
            this.cacheExpTask = gameManager.getPlugin().getServer().getScheduler().runTaskTimer(gameManager.getPlugin(), () -> RankTableUtils.cacheIntoMap(cachedExp, topExp, gameManager.getPlugin()), 1L, 20L * 20L);
        }
    }

    private void startCacheWinsTask() {
        if (this.cacheWinsTask == null) {
            LoggerUtils.logColor(gameManager.getPlugin().getLogger(), "&eCaching player win into local data. . .");
            this.cacheWinsTask = gameManager.getPlugin().getServer().getScheduler().runTaskTimer(gameManager.getPlugin(), () -> GlobalStatsTableUtils.cacheWinsIntoMap(cachedWins, topWins, topTotalWins, gameManager.getPlugin()), 1L, 20L * 20L);
        }
    }

    private void startCacheKillsTask() {
        if (this.cacheKillsTask == null) {
            LoggerUtils.logColor(gameManager.getPlugin().getLogger(), "&eCaching player kills into local data. . .");
            this.cacheKillsTask = gameManager.getPlugin().getServer().getScheduler().runTaskTimer(gameManager.getPlugin(), () -> {
                GlobalStatsTableUtils.cacheKillsIntoMap(cachedKills, topKills, topFinalKills, gameManager.getPlugin(), false);
                GlobalStatsTableUtils.cacheKillsIntoMap(cachedFinalKills, topKills, topFinalKills, gameManager.getPlugin(), true);
            }, 1L, 20L * 20L);
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
                if (!activeGame.getCachedGameData().getGame().equals(p.getWorld())) {
                    continue;
                }

                for (final Map.Entry<BedwarsTeam, ? extends TeamData<?>> entry : activeGame.getTeamManager().getDataMap().entrySet()) {
                    for (final BedwarsPlayer player1 : entry.getValue().getAll()) {
                        if (player1.getUniqueId().equals(player.getUniqueId())) {
                            final Character pChar = entry.getKey().getColorCode();
                            cachedTeamColors.put(player.getUniqueId(), new Pair<>(System.currentTimeMillis(), pChar));
                            return "&" + pChar.toString();
                        }
                    }
                }
            }
        }
        return " ";
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

    private static boolean included(final int lower, final int upper, final int n) {
        return ((n >= lower) && (upper <= n));
    }

    private String getRank(final Player player) {
        final Integer exp = cachedExp.get(player.getUniqueId());
        if (exp == null) {
            return "&7";
        }
        final int lvl = ExperienceManager.findLevelFromExp(exp);
        if (included(0, 99, lvl)) {
            return "&7";
        } else if (included(100, 199, lvl)) {
            return "&f"; // (0-99) Iron
        } else if (included(200, 299, lvl)) {
            return "&6"; // (100-199) Gold
        } else if (included(300, 399, lvl)) {
            return "&b"; // (200-299) Diamond
        } else if (included(400, 499, lvl)) {
            return "&2"; // (300-399) Emerald
        } else if (included(500, 599, lvl)) {
            return "&3"; // (400-499) Sapphire
        } else if (included(600, 699, lvl)) {
            return "&4"; // (500-599) Ruby
        } else if (included(700, 799, lvl)) {
            return "&d"; // (600-699) Crystal
        } else if (included(800, 899, lvl)) {
            return "&9"; // (700-799) Opal
        } else if (included(900, 999, lvl)) {
            return "&5"; // (800-899) Amethyst
        } else if (included(1000, Integer.MAX_VALUE, lvl)) {
            return "&u"; // (900-1000) Rainbow
        } else {
            return "";
        }
    }

    private final static Pattern topKillPattern = Pattern.compile("top_(solo|duo|quad)_kill_[0-9]+");
    private final static Pattern topFinalKillPattern = Pattern.compile("top_(solo|duo|quad)_fkill_[0-9]+");
    private final static Pattern topWinsPattern = Pattern.compile("top_(solo|duo|quad)_wins_[0-9]+");
    private final static Pattern topTotalWinsPattern = Pattern.compile("top_total_(solo|duo|quad)_wins_[0-9]+");
    private final static Pattern topLevelPattern = Pattern.compile("top_level_[0-9]+");
    private final static Pattern underscorePattern = Pattern.compile("_");

    @Override
    public final String onPlaceholderRequest(final Player player, final String identifier) {
        if (player == null) {
            return "";
        }

        switch (identifier) {
            case "version":
                return gameManager.getPlugin().getDescription().getVersion();
            case "rank":
                return getRank(player);
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

        if (topKillPattern.matcher(identifier).matches()) {
            final String[] split = underscorePattern.split(identifier);
            final int i = Integer.parseInt(split[3]);
            if (topKills.size() <= i) {
                final Pair<UUID, Integer> got = topKills.get(Gamemode.valueOf(split[1].toUpperCase())).get(i - 1);
                if (got == null || got.getB() == 0) {
                    return "";
                } else {
                    return Bukkit.getOfflinePlayer(got.getA()).getName() + " &7- &e" + got.getB();
                }
            }
        } else if (topFinalKillPattern.matcher(identifier).matches()) {
            final String[] split = underscorePattern.split(identifier);
            final int i = Integer.parseInt(split[3]);
            if (topFinalKills.size() <= i) {
                final Pair<UUID, Integer> got = topFinalKills.get(Gamemode.valueOf(split[1].toUpperCase())).get(i - 1);
                if (got == null || got.getB() == 0) {
                    return "";
                } else {
                    return Bukkit.getOfflinePlayer(got.getA()).getName() + " &7- &e" + got.getB();
                }
            }
        } else if (topWinsPattern.matcher(identifier).matches()) {
            final String[] split = underscorePattern.split(identifier);
            final int i = Integer.parseInt(split[3]);
            if (topWins.size() <= i) {
                final Pair<UUID, Integer> got = topWins.get(Gamemode.valueOf(split[1].toUpperCase())).get(i - 1);
                if (got == null || got.getB() == 0) {
                    return "";
                } else {
                    return Bukkit.getOfflinePlayer(got.getA()).getName() + " &7- &e" + got.getB();
                }
            }
        } else if (topLevelPattern.matcher(identifier).matches()) {
            final String[] split = underscorePattern.split(identifier);
            final int i = Integer.parseInt(split[2]);
            if (this.topExp.size() <= i) {
                final Pair<UUID, Integer> got = topExp.get(i - 1);
                if (got == null || got.getB() == 0) {
                    return "";
                } else {
                    return Bukkit.getOfflinePlayer(got.getA()).getName() + " &7- &e" + got.getB();
                }
            }
        } else if (topTotalWinsPattern.matcher(identifier).matches()) {
            final String[] split = underscorePattern.split(identifier);
            final int i = Integer.parseInt(split[0x04]);
            if (this.topTotalWins.size() <= i) {
                final Pair<UUID, Integer> got = topTotalWins.get(i - 1);
                if (got == null || got.getB() == 0) {
                    return "";
                } else {
                    return Bukkit.getOfflinePlayer(got.getA()).getName() + " &7- &e" + got.getB();
                }
            }
        }

        return null;
    }
}
