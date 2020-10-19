package me.thevipershow.bedwars.storage.sql.tables;

import java.sql.Connection;
import me.thevipershow.bedwars.storage.sql.TableCreator;

public final class GlobalStatsTableCreator extends TableCreator {
    public final static String TABLE = "global_stats";

    public GlobalStatsTableCreator(final Connection connection) {
        super(TABLE,
                "uuid CHARACTER(36) NOT NULL UNIQUE PRIMARY KEY," +
                        " solo_wins INT NOT NULL," +
                        " duo_wins INT NOT NULL," +
                        " quad_wins INT NOT NULL," +
                        " solo_kills INT NOT NULL," +
                        " duo_kills INT NOT NULL," +
                        " quad_kills INT NOT NULL",
                connection);
    }
}
