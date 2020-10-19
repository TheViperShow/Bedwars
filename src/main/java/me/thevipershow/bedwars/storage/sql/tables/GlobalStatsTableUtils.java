package me.thevipershow.bedwars.storage.sql.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.thevipershow.bedwars.bedwars.Gamemode;
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

    public static void cacheKillsIntoMap(final Map<UUID, Kills> map, final Plugin plugin) {

        final BukkitScheduler scheduler = plugin.getServer().getScheduler();
        final Optional<Connection> optionalConnection = MySQLDatabase.getConnection();

        optionalConnection.ifPresent(connection -> scheduler.runTaskAsynchronously(plugin, () -> {
            try (final Connection conn = connection;
                 final PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + GlobalStatsTableCreator.TABLE + ";");
                 final ResultSet rs = ps.executeQuery()) {

                final HashMap<UUID, Kills> wMap = new HashMap<>();
                while (rs.next()) {
                    final UUID uuid = UUID.fromString(rs.getString("uuid"));
                    final Kills wins = new Kills(rs.getInt("solo_kills"), rs.getInt("duo_kills"), rs.getInt("quad_kills"));
                    wMap.put(uuid, wins);
                }

                scheduler.runTask(plugin, () -> {
                    map.clear();
                    map.putAll(wMap);
                });
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void cacheWinsIntoMap(final Map<UUID, Wins> map, final Plugin plugin) {

        final BukkitScheduler scheduler = plugin.getServer().getScheduler();
        final Optional<Connection> optionalConnection = MySQLDatabase.getConnection();

        optionalConnection.ifPresent(connection -> scheduler.runTaskAsynchronously(plugin, () -> {
            try (final Connection conn = connection;
                final PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + GlobalStatsTableCreator.TABLE + ";");
                final ResultSet rs = ps.executeQuery()) {

                final HashMap<UUID, Wins> wMap = new HashMap<>();
                while (rs.next()) {
                    final UUID uuid = UUID.fromString(rs.getString("uuid"));
                    final Wins wins = new Wins(rs.getInt("solo_wins"), rs.getInt("duo_wins"), rs.getInt("quad_wins"));
                    wMap.put(uuid, wins);
                }

                scheduler.runTask(plugin, () -> {
                    map.clear();
                    map.putAll(wMap);
                });
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void increaseKills(final Gamemode gamemode, final Plugin plugin, final UUID uuid) {
        final Optional<Connection> conn = MySQLDatabase.getConnection();

        conn.ifPresent(connection -> {
            String update = null;
            Integer replace = null;
            switch (gamemode) {
                case SOLO:
                    replace = 5;
                    update = "solo_kills";
                    break;
                case DUO:
                    replace = 6;
                    update = "duo_kills";
                    break;
                case QUAD:
                    replace = 7;
                    update = "quad_kills";
                    break;
                default:
                    break;
            }

            final String finalUpdate = update;
            final int finalReplace = replace;
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try (final Connection con = connection;
                     final PreparedStatement ps = con.prepareStatement(
                             "INSERT INTO " + GlobalStatsTableCreator.TABLE + " (uuid, solo_wins, duo_wins, quad_wins, solo_kills, duo_kills, quad_kills) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + finalUpdate + " = " + finalUpdate + " ?;")) {

                    ps.setString(1, uuid.toString());
                    for (int i = 2; i <= 7; i++) {
                        ps.setInt(i, finalReplace == i ? 1 : 0);
                    }
                    ps.setInt(8, 1);
                    ps.executeUpdate();
                } catch (final SQLException e ) {
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
                            "INSERT INTO " + GlobalStatsTableCreator.TABLE + " (uuid, solo_wins, duo_wins, quad_wins, solo_kills, duo_kills, quad_kills) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + finalUpdate + " = " + finalUpdate + " ?;")) {

                    ps.setString(1, uuid.toString());
                    for (int i = 2; i <= 7; i++) {
                        ps.setInt(i, finalReplace == i ? 1 : 0);
                    }
                    ps.setInt(8, 1);
                    ps.executeUpdate();
                } catch (final SQLException e ) {
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
