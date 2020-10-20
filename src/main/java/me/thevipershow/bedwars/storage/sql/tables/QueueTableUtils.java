package me.thevipershow.bedwars.storage.sql.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.storage.sql.MySQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class QueueTableUtils {

    public static void addVillager(final Villager villager, final Gamemode gamemode, final Plugin plugin) {
        final UUID uuid = villager.getUniqueId();

        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();
        connectionOptional.ifPresent(c -> {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try (final Connection conn = c;
                     PreparedStatement preparedStatement = conn.prepareStatement(
                             "INSERT INTO " + QueueVillagerTableCreator.TABLE +
                                     " (uuid, gamemode) VALUES (?, ?) ON DUPLICATE KEY UPDATE gamemode = ?;")) {
                    preparedStatement.setString(1, uuid.toString());
                    preparedStatement.setString(2, gamemode.name());
                    preparedStatement.setString(3, gamemode.name());
                    preparedStatement.executeUpdate();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public static CompletableFuture<Boolean> removeVillager(final Villager villager, final Plugin plugin) {
        final CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        final BukkitScheduler bukkitScheduler = plugin.getServer().getScheduler();
        final Optional<Connection> optionalConnection = MySQLDatabase.getConnection();

        if (optionalConnection.isPresent()) {
            bukkitScheduler.runTaskAsynchronously(plugin, () -> {

                try (final Connection conn = optionalConnection.get();
                     final PreparedStatement ps = conn.prepareStatement("DELETE FROM " + QueueVillagerTableCreator.TABLE + " WHERE uuid=?;")) {
                    ps.setString(1, villager.getUniqueId().toString());
                    final int result = ps.executeUpdate();
                    bukkitScheduler.runTask(plugin, () -> completableFuture.complete(result > 0));
                } catch (final SQLException e) {
                    e.printStackTrace();
                    completableFuture.completeExceptionally(e);
                }

            });
        } else {
            completableFuture.completeExceptionally(new SQLException("Could not get a connection."));
        }

        return completableFuture;
    }

    public static CompletableFuture<Optional<Gamemode>> getVillagerGamemode(final UUID villagerUUID, final Plugin plugin) {
        final CompletableFuture<Optional<Gamemode>> completableFuture = new CompletableFuture<>();
        final Optional<Connection> connectionOptional = MySQLDatabase.getConnection();
        final BukkitScheduler scheduler = plugin.getServer().getScheduler();

        if (connectionOptional.isPresent()) {
            scheduler.runTaskAsynchronously(plugin, () -> {

                try (Connection conn = connectionOptional.get();
                     PreparedStatement preparedStatement = conn.prepareStatement(
                             "SELECT (gamemode) FROM " + QueueVillagerTableCreator.TABLE + " WHERE uuid = ?;")) {
                    preparedStatement.setString(1, villagerUUID.toString());
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            final String g = resultSet.getString("gamemode");
                            if (g == null) {
                                scheduler.runTask(plugin, () -> completableFuture.complete(Optional.empty()));
                            } else {
                                scheduler.runTask(plugin, () -> completableFuture.complete(Optional.of(Gamemode.valueOf(g))));
                            }
                        }
                    }
                } catch (final SQLException e) {
                    e.printStackTrace();
                    completableFuture.completeExceptionally(e);
                }
            });
        } else {
            completableFuture.completeExceptionally(new SQLException("Could not find a connection."));
        }

        return completableFuture;
    }


}
