package me.thevipershow.bedwars.storage.sql.tables;

import java.sql.Connection;
import me.thevipershow.bedwars.storage.sql.TableCreator;

public final class QueueVillagerTableCreator extends TableCreator {
    public final static String TABLE = "queue_villagers";

    public QueueVillagerTableCreator(Connection connection) {
        super(TABLE, "uuid CHARACTER(36) NOT NULL UNIQUE PRIMARY KEY, gamemode VARCHAR(16) NOT NULL", connection);
    }
}
