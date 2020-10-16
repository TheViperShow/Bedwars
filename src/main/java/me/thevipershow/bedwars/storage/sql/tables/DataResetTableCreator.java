package me.thevipershow.bedwars.storage.sql.tables;

import java.sql.Connection;
import me.thevipershow.bedwars.storage.sql.TableCreator;

public final class DataResetTableCreator extends TableCreator {

    public final static String TABLE = "data_reset";

    public DataResetTableCreator(final Connection connection) {
        super(TABLE,
                "cleaner CHARACTER(128) NOT NULL UNIQUE PRIMARY KEY," +
                        " daily_reset_time BIGINT," +
                        " weekly_reset_time BIGINT",
                connection);
    }
}
