package me.thevipershow.bedwars.storage.sql.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.thevipershow.bedwars.bedwars.Gamemode;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class QueueTableUtils {

    public static void addVillager(final Connection connection, final Villager villager, final Gamemode gamemode) {
        try (Connection conn = connection;
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "INSERT INTO " + QueueVillagerTableCreator.TABLE +
                             " (uuid, gamemode) VALUES (?, ?) ON DUPLICATE KEY UPDATE gamemode = ?;")) {
            preparedStatement.setString(1, villager.getUniqueId().toString());
            preparedStatement.setString(2, gamemode.name());
            preparedStatement.setString(3, gamemode.name());
            preparedStatement.executeUpdate();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public static CompletableFuture<Boolean> removeVillager(final Connection connection, final Villager villager) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = connection;
                 PreparedStatement preparedStatement = conn.prepareStatement(
                         "DELETE FROM " + QueueVillagerTableCreator.TABLE + " WHERE uuid=?;")
            ) {
                preparedStatement.setString(1, villager.getUniqueId().toString());
                return preparedStatement.executeUpdate() > 0;
            } catch (final SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public static CompletableFuture<Boolean> removeVillager(final Connection connection, final Villager villager, final Plugin plugin) {

        final CompletableFuture<Boolean> hasRemoved = new CompletableFuture<>();
        final BukkitScheduler scheduler = plugin.getServer().getScheduler();

        scheduler.runTaskAsynchronously(plugin, () -> {
            try (Connection conn = connection;
                 PreparedStatement preparedStatement = conn.prepareStatement(
                         "DELETE FROM " + QueueVillagerTableCreator.TABLE + " WHERE uuid=?;")
            ) {
                preparedStatement.setString(1, villager.getUniqueId().toString());
                final boolean success = preparedStatement.executeUpdate() > 0;
                scheduler.runTask(plugin, () -> hasRemoved.complete(success));
            } catch (final SQLException e) {
                e.printStackTrace();
                hasRemoved.completeExceptionally(e);
            }
        });

        return hasRemoved;
    }

    public static CompletableFuture<Optional<Gamemode>> getVillagerGamemode(final UUID villagerUUID, final Connection connection) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = connection;
                 PreparedStatement preparedStatement = conn.prepareStatement(
                         "SELECT (gamemode) FROM " + QueueVillagerTableCreator.TABLE + " WHERE uuid = ?;")) {
                preparedStatement.setString(1, villagerUUID.toString());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String g = resultSet.getString("gamemode");
                        if (g == null) {
                            return Optional.empty();
                        }
                        return Optional.of(Gamemode.valueOf(g));
                    }
                }
            } catch (final SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });
    }


}
