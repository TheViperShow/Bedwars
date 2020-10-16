package me.thevipershow.bedwars.storage.sql.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.thevipershow.bedwars.game.Pair;
import me.thevipershow.bedwars.storage.sql.MySQLDatabase;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class QuestsTableUtils {

    private QuestsTableUtils() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static void clearWeeklyData(final Plugin plugin) {
        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();
        connectionOptional.ifPresent(conn -> plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (final Connection c = conn;
                 final PreparedStatement ps = c.prepareStatement("UPDATE " + WeeklyQuestsTableCreator.TABLE + " SET beds_broken = ?;")) {
                ps.setInt(0x01, 0x00);
                ps.executeUpdate();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void clearDailyData(final Plugin plugin) {
        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();
        connectionOptional.ifPresent(conn -> plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (final Connection c = conn;
                 final PreparedStatement ps = c.prepareStatement("UPDATE " + DailyQuestsTableCreator.TABLE + " SET win_first = ?, games_played = ?;")) {
                ps.setBoolean(0x01, false);
                ps.setInt(0x02, 0x00);
                ps.executeUpdate();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void increaseBrokenBeds(final Plugin plugin, final Player player) {
        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();
        connectionOptional.ifPresent(conn -> plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (final Connection c = conn;
                 final PreparedStatement ps = c.prepareStatement("INSERT INTO " + WeeklyQuestsTableCreator.TABLE + " (uuid, username, beds_broken) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE username = ?, beds_broken = beds_broken + ?;")) {
                ps.setString(0x01, player.getUniqueId().toString());
                ps.setString(0x02, player.getName());
                ps.setInt(0x03, 0x01);
                ps.setString(0x04, player.getName());
                ps.setInt(0x05, 0x01);
                ps.executeUpdate();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    public static CompletableFuture<Boolean> getDailyFirstWin(final Plugin plugin, final Player player) {

        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();

        if (connectionOptional.isPresent()) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

                try (final Connection conn = connectionOptional.get();
                     final PreparedStatement ps = conn.prepareStatement("SELECT (win_first) FROM " + DailyQuestsTableCreator.TABLE + " WHERE uuid = ?;")) {

                    ps.setString(1, player.getUniqueId().toString());
                    try (final ResultSet rs = ps.executeQuery()) {
                        boolean firstWin;
                        if (rs.next()) {
                            firstWin = rs.getBoolean("win_first");
                            plugin.getServer().getScheduler().runTask(plugin, () -> future.complete(firstWin));
                        } else {
                            plugin.getServer().getScheduler().runTask(plugin, () -> future.complete(null));
                        }
                    }
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });
        } else {
            future.completeExceptionally(new SQLException("Could not get connection."));
        }
        return future;
    }

    public static void setDailyFirstWin(final Plugin plugin, final Player player) {
        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();
        connectionOptional.ifPresent(conn -> plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (final Connection c = conn;
                 final PreparedStatement ps = c.prepareStatement("INSERT INTO " + DailyQuestsTableCreator.TABLE + " (uuid, username, win_first, games_played) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = ?, win_first = ?;")) {
                ps.setString(0x01, player.getUniqueId().toString());
                ps.setString(0x02, player.getName());
                ps.setBoolean(0x03, true);
                ps.setInt(0x04, 0x00);
                ps.setString(0x05, player.getName());
                ps.setBoolean(0x06, true);
                ps.executeUpdate();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    public static CompletableFuture<Integer> getDailyGamesPlayed(final Plugin plugin, final Player player) {

        final CompletableFuture<Integer> future = new CompletableFuture<>();
        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();

        if (connectionOptional.isPresent()) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

                try (final Connection conn = connectionOptional.get();
                     final PreparedStatement ps = conn.prepareStatement("SELECT (games_played) FROM " + DailyQuestsTableCreator.TABLE + " WHERE uuid = ?;")) {

                    ps.setString(1, player.getUniqueId().toString());
                    try (final ResultSet rs = ps.executeQuery()) {
                        int firstWin;
                        if (rs.next()) {
                            firstWin = rs.getInt("games_played");
                            plugin.getServer().getScheduler().runTask(plugin, () -> future.complete(firstWin));
                        } else {
                            plugin.getServer().getScheduler().runTask(plugin, () -> future.complete(null));
                        }
                    }
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });
        } else {
            future.completeExceptionally(new SQLException("Could not get connection."));
        }
        return future;
    }

    public static void increaseGamesPlayed(final Plugin plugin, final Player player) {
        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();
        connectionOptional.ifPresent(conn -> plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (final Connection c = conn;
                 final PreparedStatement ps = c.prepareStatement("INSERT INTO " + DailyQuestsTableCreator.TABLE + " (uuid, username, win_first, games_played) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = ?, games_played = games_played + ?;")) {
                ps.setString(0x01, player.getUniqueId().toString());
                ps.setString(0x02, player.getName());
                ps.setBoolean(0x03, false);
                ps.setInt(0x04, 0x01);
                ps.setString(0x05, player.getName());
                ps.setInt(0x06, 0x01);
                ps.executeUpdate();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    public static CompletableFuture<Integer> getBedsBroken(final UUID uuid, final Plugin plugin) {

        final CompletableFuture<Integer> future = new CompletableFuture<>();
        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();

        if (connectionOptional.isPresent()) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

                try (final Connection conn = connectionOptional.get();
                     final PreparedStatement ps = conn.prepareStatement("SELECT (beds_broken) FROM " + WeeklyQuestsTableCreator.TABLE + " WHERE uuid = ?;")) {

                    ps.setString(1, uuid.toString());
                    try (final ResultSet rs = ps.executeQuery()) {
                        int return_ = -1;
                        if (rs.next()) {
                            return_ = rs.getInt("beds_broken");
                        }
                        final int finalReturn_ = return_;
                        plugin.getServer().getScheduler().runTask(plugin, () -> future.complete(finalReturn_));
                    }
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });
        } else {
            future.completeExceptionally(new SQLException("Could not get connection."));
        }
        return future;

    }

    /**
     * Get weekly data (wins\games).
     *
     * @param uuid   uuid of target
     * @param plugin a plugin instance.
     * @return null if row absent, 2 values otherwise.
     */
    public static CompletableFuture<Pair<Integer, Integer>> getWeeklyData(final UUID uuid, final Plugin plugin) {

        final CompletableFuture<Pair<Integer, Integer>> future = new CompletableFuture<>();
        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();

        if (connectionOptional.isPresent()) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

                try (final Connection conn = connectionOptional.get();
                     final PreparedStatement ps = conn.prepareStatement("SELECT (win_first, games_played) FROM " + DailyQuestsTableCreator.TABLE + " WHERE uuid = ?;")) {

                    ps.setString(1, uuid.toString());
                    try (final ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            final Pair<Integer, Integer> values = new Pair<>(rs.getInt("win_first"), rs.getInt("games_played"));
                            plugin.getServer().getScheduler().runTask(plugin, () -> future.complete(values));
                        } else {
                            plugin.getServer().getScheduler().runTask(plugin, () -> future.complete(null));
                        }
                    }
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });
        } else {
            future.completeExceptionally(new SQLException("Could not get connection."));
        }
        return future;
    }
}
