package me.thevipershow.bedwars.storage.sql.tables;

import java.sql.Connection;
import me.thevipershow.bedwars.storage.sql.TableCreator;

public final class WeeklyQuestsTableCreator extends TableCreator {
    public static final String TABLE = "weekly_quests_data";

    public WeeklyQuestsTableCreator(final Connection connection) {
        super(TABLE,
                "uuid CHARACTER(36) NOT NULL UNIQUE PRIMARY KEY," +
                        " username CHARACTER(16) NOT NULL UNIQUE," +
                        " beds_broken INT NOT NULL",
                connection);
    }


}
