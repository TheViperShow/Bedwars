package me.thevipershow.aussiebedwars.storage.sql.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.thevipershow.aussiebedwars.storage.sql.MySQLDatabase;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class RankTableUtils {

    public static void rewardPlayerExp(final Player player, final int exp) {
        final Optional<Connection> conn = MySQLDatabase.getConnection();
        conn.ifPresent(connection -> {
            try (final Connection c = connection;
                 final PreparedStatement ps = c.prepareStatement("INSERT INTO " + RanksTableCreator.TABLE +
                         " (uuid, username, exp) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE username = ?, exp = exp + ?;")) {

                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, player.getName());
                ps.setInt(3, exp);
                ps.setString(4, player.getName());
                ps.setInt(5, exp);
                ps.executeUpdate();
            } catch (final SQLException e) {
                e.printStackTrace();
            }

        });
    }

    public static CompletableFuture<Integer> getPlayerExp(final UUID uuid, final Plugin plugin) {

        final CompletableFuture<Integer> value = new CompletableFuture<>();
        final BukkitScheduler scheduler = plugin.getServer().getScheduler();
        final Optional<Connection> optionalConnection = MySQLDatabase.getConnection();

        if (optionalConnection.isPresent()) {

            scheduler.runTaskAsynchronously(plugin, () -> {
                try (final Connection conn = optionalConnection.get();
                final PreparedStatement ps = conn.prepareStatement(
                        "SELECT (exp) FROM " + RanksTableCreator.TABLE + " WHERE uuid = ?;"
                )) {
                    ps.setString(1, uuid.toString());
                    try (final ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            final int v = rs.getInt("exp");
                            value.complete(v);
                        }
                    }
                } catch (final SQLException e) {
                    e.printStackTrace();
                    value.completeExceptionally(e);
                }
            });

        } else {
            value.completeExceptionally(new SQLException("Could not connect to db."));
        }
        return value;
    }

    public static CompletableFuture<Integer> getPlayerExp(final String name, final Plugin plugin) {

        final CompletableFuture<Integer> value = new CompletableFuture<>();
        final BukkitScheduler scheduler = plugin.getServer().getScheduler();
        final Optional<Connection> optionalConnection = MySQLDatabase.getConnection();

        if (optionalConnection.isPresent()) {

            scheduler.runTaskAsynchronously(plugin, () -> {
                try (final Connection conn = optionalConnection.get();
                     final PreparedStatement ps = conn.prepareStatement(
                             "SELECT (exp) FROM " + RanksTableCreator.TABLE + " WHERE username = ?;"
                     )) {
                    ps.setString(1, name);
                    try (final ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            final int v = rs.getInt("exp");
                            value.complete(v);
                        }
                    }
                } catch (final SQLException e) {
                    e.printStackTrace();
                    value.completeExceptionally(e);
                }
            });

        } else {
            value.completeExceptionally(new SQLException("Could not connect to db."));
        }
        return value;
    }
}
