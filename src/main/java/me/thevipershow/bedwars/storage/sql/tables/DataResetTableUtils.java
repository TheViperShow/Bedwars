package me.thevipershow.bedwars.storage.sql.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import me.thevipershow.bedwars.storage.sql.MySQLDatabase;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class DataResetTableUtils {

    public static void updateDailyTime(final Plugin plugin) {

        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();
        if (!connectionOptional.isPresent()) {
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (final Connection conn = connectionOptional.get();
                 final PreparedStatement ps = conn.prepareStatement("INSERT INTO " + DataResetTableCreator.TABLE + " (cleaner, daily_reset_time) VALUES (?, ?) ON DUPLICATE KEY UPDATE daily_reset_time = ?;")) {

                ps.setString(1, "server");
                final long time = System.currentTimeMillis();
                ps.setLong(2, time);
                ps.setLong(3, time);
                ps.executeUpdate();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void updateWeeklyTime(final Plugin plugin) {

        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();
        if (!connectionOptional.isPresent()) {
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (final Connection conn = connectionOptional.get();
                 final PreparedStatement ps = conn.prepareStatement("INSERT INTO " + DataResetTableCreator.TABLE + " (cleaner, weekly_reset_time) VALUES (?, ?) ON DUPLICATE KEY UPDATE weekly_reset_time = ?;")) {

                ps.setString(1, "server");
                final long time = System.currentTimeMillis();
                ps.setLong(2, time);
                ps.setLong(3, time);
                ps.executeUpdate();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Long> getDailyTime(final Plugin plugin) {

        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();
        final CompletableFuture<Long> longCompletableFuture = new CompletableFuture<>();

        if (connectionOptional.isPresent()) {

            final BukkitScheduler scheduler = plugin.getServer().getScheduler();
            scheduler.runTaskAsynchronously(plugin, () -> {

                try (final Connection conn = connectionOptional.get();
                     final PreparedStatement ps = conn.prepareStatement("SELECT daily_reset_time FROM " + DataResetTableCreator.TABLE + " WHERE cleaner = ?;")) {
                    ps.setString(1, "server");
                    try (final ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            final Long l = rs.getLong("daily_reset_time");
                            scheduler.runTask(plugin, () -> longCompletableFuture.complete(l));
                        }
                    }
                } catch (final SQLException e) {
                    e.printStackTrace();
                    longCompletableFuture.completeExceptionally(e);
                }
            });

        } else {
            longCompletableFuture.completeExceptionally(new SQLException("could not get connection."));
        }

        return longCompletableFuture;
    }

    public static CompletableFuture<Long> getWeeklyTime(final Plugin plugin) {

        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();
        final CompletableFuture<Long> longCompletableFuture = new CompletableFuture<>();

        if (connectionOptional.isPresent()) {

            final BukkitScheduler scheduler = plugin.getServer().getScheduler();
            scheduler.runTaskAsynchronously(plugin, () -> {

                try (final Connection conn = connectionOptional.get();
                     final PreparedStatement ps = conn.prepareStatement("SELECT weekly_reset_time FROM " + DataResetTableCreator.TABLE + " WHERE cleaner = ?;")) {
                    ps.setString(1, "server");
                    try (final ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            final Long l = rs.getLong("weekly_reset_time");
                            scheduler.runTask(plugin, () -> longCompletableFuture.complete(l));
                        }
                    }
                } catch (final SQLException e) {
                    e.printStackTrace();
                    longCompletableFuture.completeExceptionally(e);
                }
            });

        } else {
            longCompletableFuture.completeExceptionally(new SQLException("could not get connection."));
        }

        return longCompletableFuture;
    }
}
