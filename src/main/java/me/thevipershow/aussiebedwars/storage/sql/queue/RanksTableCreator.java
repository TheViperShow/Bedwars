package me.thevipershow.aussiebedwars.storage.sql.queue;

import java.sql.Connection;
import me.thevipershow.aussiebedwars.storage.sql.TableCreator;

public final class RanksTableCreator extends TableCreator {
    public final static String TABLE = "ranks";

    public RanksTableCreator(final Connection connection) {
        super(TABLE,
                "uuid CHARACTER(36) NOT NULL UNIQUE PRIMARY KEY, username CHARACTER(16) NOT NULL UNIQUE, exp INT NOT NULL",
                connection);
    }
}
