package me.thevipershow.aussiebedwars.storage.sql.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import org.bukkit.entity.Villager;

public final class QueueTableUtils {

    public static void addVillager(final Connection connection, final Villager villager, final Gamemode gamemode) {
        try (Connection conn = connection;
        PreparedStatement preparedStatement = conn.prepareStatement(
                "INSERT INTO " + QueueVillagerTableCreator.TABLE +
                        " (uuid, gamemode) VALUES (?, ? );")) {
            preparedStatement.setString(1, villager.getUniqueId().toString());
            preparedStatement.setString(2, gamemode.name());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
