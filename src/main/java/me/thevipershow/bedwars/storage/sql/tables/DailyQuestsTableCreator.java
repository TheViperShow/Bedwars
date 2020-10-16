package me.thevipershow.bedwars.storage.sql.tables;

import java.sql.Connection;
import me.thevipershow.bedwars.storage.sql.TableCreator;

public final class DailyQuestsTableCreator extends TableCreator {

    public static final String TABLE = "daily_quests_data";

    public DailyQuestsTableCreator(final Connection connection) {
        super(TABLE,
                "uuid CHARACTER(36) NOT NULL UNIQUE PRIMARY KEY," +
                        " username CHARACTER(16) NOT NULL UNIQUE," +
                        " win_first BOOLEAN NOT NULL," +
                        " games_played INT NOT NULL",
                connection);
    }
}
