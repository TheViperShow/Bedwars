package me.thevipershow.aussiebedwars.storage.sql;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import me.thevipershow.aussiebedwars.storage.sql.queue.QueueVillagerTableCreator;
import org.bukkit.plugin.java.JavaPlugin;

public final class SQLiteDatabase extends Database {
    private final File dataFolder;
    private final File databaseFile;
    private final static String SQLITE_FILENAME = "aussiebedwars-data.sqlite";

    public SQLiteDatabase(JavaPlugin plugin) {
        super(plugin, QueueVillagerTableCreator.class);
        this.dataFolder = plugin.getDataFolder();
        this.databaseFile = new File(this.dataFolder, SQLITE_FILENAME);
        setConnectionUrl(String.format("jdbc:sqlite:%s", this.databaseFile.getAbsolutePath()));
        createTables();
    }

    public static Optional<Connection> getConnection() {
        try {
            return Optional.of(DriverManager.getConnection(getConnectionUrl()));
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void createTables() {
        for (Class<? extends TableCreator> tableCreator : tableCreators) {
            try {
                final Constructor<? extends TableCreator> constructor = tableCreator.getConstructor(Connection.class);
                final Optional<Connection> connection = getConnection();
                if (connection.isPresent()) {
                    constructor.newInstance(connection.get()).createTable();
                }
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public File getDatabaseFile() {
        return databaseFile;
    }
}
