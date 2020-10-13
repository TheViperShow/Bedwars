package me.thevipershow.aussiebedwars.storage.sql.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import me.thevipershow.aussiebedwars.storage.sql.MySQLDatabase;
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
}
