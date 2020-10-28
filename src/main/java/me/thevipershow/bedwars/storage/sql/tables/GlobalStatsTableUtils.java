package me.thevipershow.bedwars.storage.sql.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.thevipershow.bedwars.bedwars.Gamemode;
import static me.thevipershow.bedwars.bedwars.Gamemode.DUO;
import static me.thevipershow.bedwars.bedwars.Gamemode.QUAD;
import static me.thevipershow.bedwars.bedwars.Gamemode.SOLO;
import me.thevipershow.bedwars.game.Pair;
import me.thevipershow.bedwars.storage.sql.MySQLDatabase;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class GlobalStatsTableUtils {

    public static final class Kills {
        private final int soloKills, duoKills, quadKills;

        public Kills(final int soloKills, final int duoKills, final int quadKills) {
            this.soloKills = soloKills;
            this.duoKills = duoKills;
            this.quadKills = quadKills;
        }

        public final int getSum() {
            return getSoloKills() + getDuoKills() + getQuadKills();
        }

        public final int getKills(final Gamemode gamemode) {
            switch (gamemode) {
                case SOLO:
                    return getSoloKills();
                case DUO:
                    return getDuoKills();
                case QUAD:
                    return getQuadKills();
                default:
                    throw new UnsupportedOperationException("illegal gamemode.");
            }
        }

        public final int getSoloKills() {
            return soloKills;
        }

        public final int getDuoKills() {
            return duoKills;
        }

        public final int getQuadKills() {
            return quadKills;
        }
    }

    public static final class Wins {
        private final int soloWins, duoWins, quadWins;

        public Wins(final int soloWins, final int duoWins, final int quadWins) {
            this.soloWins = soloWins;
            this.duoWins = duoWins;
            this.quadWins = quadWins;
        }

        public final int getSum() {
            return getSoloWins() + getDuoWins() + getQuadWins();
        }

        public int getWin(final Gamemode gamemode) {
            switch (gamemode) {
                case SOLO:
                    return getSoloWins();
                case DUO:
                    return getDuoWins();
                case QUAD:
                    return getQuadWins();
                default:
                    throw new UnsupportedOperationException("illegal gamemode.");
            }
        }

        public final int getSoloWins() {
            return soloWins;
        }

        public final int getDuoWins() {
            return duoWins;
        }

        public final int getQuadWins() {
            return quadWins;
        }
    }

    public static void cacheKillsIntoMap(final LinkedHashMap<UUID, Kills> map, final Map<Gamemode, LinkedList<Pair<UUID, Integer>>> top, final Map<Gamemode, LinkedList<Pair<UUID, Integer>>> topFinal, final Plugin plugin, final boolean finalKill) {

        final BukkitScheduler scheduler = plugin.getServer().getScheduler();
        final Optional<Connection> optionalConnection = MySQLDatabase.getConnection();

        optionalConnection.ifPresent(connection -> scheduler.runTaskAsynchronously(plugin, () -> {
            try (final Connection conn = connection;
                 final PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + GlobalStatsTableCreator.TABLE + ";");
                 final ResultSet rs = ps.executeQuery()) {

                final Map<UUID, Kills> wMap = new HashMap<>();
                while (rs.next()) {
                    final UUID uuid = UUID.fromString(rs.getString("uuid"));
                    if (finalKill) {
                        final Kills killz = new Kills(rs.getInt("solo_kills"), rs.getInt("duo_kills"), rs.getInt("quad_kills"));
                        wMap.put(uuid, killz);
                    } else {
                        final Kills finalKillz = new Kills(rs.getInt("solo_fkills"), rs.getInt("duo_fkills"), rs.getInt("quad_fkills"));
                        wMap.put(uuid, finalKillz);
                    }
                }

                scheduler.runTask(plugin, () -> {
                    map.clear();
                    map.putAll(wMap);

                    final LinkedList<Pair<UUID, Integer>> topSolo = new LinkedList<>(), topDuo = new LinkedList<>(), topQuad = new LinkedList<>();

                    top.clear();
                    topFinal.clear();

                    wMap.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue((a, b) -> Math.max(a.getSoloKills(), b.getSoloKills())))
                            .forEachOrdered(o -> topSolo.offerLast(new Pair<>(o.getKey(), o.getValue().getSoloKills())));
                    wMap.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue((a, b) -> Math.max(a.getDuoKills(), b.getDuoKills())))
                            .forEachOrdered(o -> topDuo.offerLast(new Pair<>(o.getKey(), o.getValue().getDuoKills())));
                    wMap.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue((a, b) -> Math.max(a.getQuadKills(), b.getQuadKills())))
                            .forEachOrdered(o -> topQuad.offerLast(new Pair<>(o.getKey(), o.getValue().getQuadKills())));

                    if (!finalKill) {
                        top.put(SOLO, topSolo);
                        top.put(Gamemode.DUO, topDuo);
                        top.put(Gamemode.QUAD, topQuad);
                    } else {
                        topFinal.put(SOLO, topSolo);
                        topFinal.put(Gamemode.DUO, topDuo);
                        topFinal.put(Gamemode.QUAD, topQuad);
                    }

                });
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void cacheWinsIntoMap(final LinkedHashMap<UUID, Wins> map, Map<Gamemode, LinkedList<Pair<UUID, Integer>>> top, final Plugin plugin) {

        final BukkitScheduler scheduler = plugin.getServer().getScheduler();
        final Optional<Connection> optionalConnection = MySQLDatabase.getConnection();

        optionalConnection.ifPresent(connection -> scheduler.runTaskAsynchronously(plugin, () -> {
            try (final Connection conn = connection;
                 final PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + GlobalStatsTableCreator.TABLE + ";");
                 final ResultSet rs = ps.executeQuery()) {

                final Map<UUID, Wins> wMap = new HashMap<>();
                while (rs.next()) {
                    final UUID uuid = UUID.fromString(rs.getString("uuid"));
                    final Wins wins = new Wins(rs.getInt("solo_wins"), rs.getInt("duo_wins"), rs.getInt("quad_wins"));
                    wMap.put(uuid, wins);
                }

                scheduler.runTask(plugin, () -> {
                    map.clear();
                    map.putAll(wMap);

                    top.clear();

                    final LinkedList<Pair<UUID, Integer>> soloWin = new LinkedList<>(), duoWin = new LinkedList<>(), quadWin = new LinkedList<>();
                    wMap.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue((a, b) -> Math.max(a.getSoloWins(), b.getSoloWins())))
                            .forEachOrdered(o -> soloWin.offerLast(new Pair<>(o.getKey(), o.getValue().getSoloWins())));
                    wMap.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue((a, b) -> Math.max(a.getDuoWins(), b.getDuoWins())))
                            .forEachOrdered(o -> duoWin.offerLast(new Pair<>(o.getKey(), o.getValue().getDuoWins())));
                    wMap.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue((a, b) -> Math.max(a.getQuadWins(), b.getQuadWins())))
                            .forEachOrdered(o -> quadWin.offerLast(new Pair<>(o.getKey(), o.getValue().getQuadWins())));

                    top.put(SOLO, soloWin);
                    top.put(DUO, duoWin);
                    top.put(QUAD, quadWin);
                });

            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void increaseKills(final Gamemode gamemode, final Plugin plugin, final UUID uuid, final boolean finalKill) {
        final Optional<Connection> conn = MySQLDatabase.getConnection();

        conn.ifPresent(connection -> {
            String update = null;
            Integer replace = null;
            switch (gamemode) {
                case SOLO:
                    replace = 5;
                    update = !finalKill ? "solo_kills" : "solo_fkills";
                    break;
                case DUO:
                    replace = 6;
                    update = !finalKill ? "duo_kills" : "duo_fkills";
                    break;
                case QUAD:
                    replace = 7;
                    update = !finalKill ? "quad_kills" : "quad_fkills";
                    break;
                default:
                    break;
            }

            final String finalUpdate = update;
            final int finalReplace = replace;
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try (final Connection con = connection;
                     final PreparedStatement ps = con.prepareStatement(
                             "INSERT INTO " + GlobalStatsTableCreator.TABLE + " (uuid, solo_wins, duo_wins, quad_wins, solo_kills, duo_kills, quad_kills, solo_fkills, duo_fkills, quad_fkills) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " + finalUpdate + " = " + finalUpdate + " + ?;")) {

                    ps.setString(1, uuid.toString());
                    for (int i = 2; i <= 10; i++) {
                        ps.setInt(i, finalReplace == i ? 1 : 0);
                    }
                    ps.setInt(11, 1);
                    ps.executeUpdate();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public static void increaseWin(final Gamemode gamemode, final Plugin plugin, final UUID uuid) {
        final Optional<Connection> conn = MySQLDatabase.getConnection();

        conn.ifPresent(connection -> {
            String update = null;
            Integer replace = null;
            switch (gamemode) {
                case SOLO:
                    replace = 2;
                    update = "solo_wins";
                    break;
                case DUO:
                    replace = 3;
                    update = "duo_wins";
                    break;
                case QUAD:
                    replace = 4;
                    update = "quad_wins";
                    break;
                default:
                    break;
            }

            final String finalUpdate = update;
            final int finalReplace = replace;
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try (final Connection con = connection;
                     final PreparedStatement ps = con.prepareStatement(
                             "INSERT INTO " + GlobalStatsTableCreator.TABLE + " (uuid, solo_wins, duo_wins, quad_wins, solo_kills, duo_kills, quad_kills, solo_fkills, duo_fkills, quad_fkills) VALUES (?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + finalUpdate + " = " + finalUpdate + " + ?;")) {

                    ps.setString(1, uuid.toString());
                    for (int i = 2; i <= 10; i++) {
                        ps.setInt(i, finalReplace == i ? 1 : 0);
                    }
                    ps.setInt(11, 1);
                    ps.executeUpdate();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public static CompletableFuture<Wins> getPlayerWins(final UUID uuid, final Plugin plugin) {

        final BukkitScheduler scheduler = plugin.getServer().getScheduler();
        final CompletableFuture<Wins> winsFuture = new CompletableFuture<>();
        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();

        if (connectionOptional.isPresent()) {
            try (final Connection conn = connectionOptional.get();
                 final PreparedStatement ps = conn.prepareStatement(
                         "SELECT (solo_wins, duo_wins, quad_wins) FROM " + GlobalStatsTableCreator.TABLE + " WHERE uuid = ?;")) {
                ps.setString(1, uuid.toString());
                try (final ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        final int sW = rs.getInt("solo_wins"), dW = rs.getInt("duo_wins"), qW = rs.getInt("quad_wins");
                        final Wins wins = new Wins(sW, dW, qW);
                        scheduler.runTask(plugin, () -> winsFuture.complete(wins));
                    } else {
                        scheduler.runTask(plugin, () -> winsFuture.complete(null));
                    }
                }
            } catch (final SQLException e) {
                e.printStackTrace();
                winsFuture.completeExceptionally(e);
            }
        } else {
            winsFuture.completeExceptionally(new SQLException("connection not available."));
        }

        return winsFuture;
    }
}
