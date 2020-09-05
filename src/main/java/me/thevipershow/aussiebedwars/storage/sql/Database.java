package me.thevipershow.aussiebedwars.storage.sql;

import java.sql.Connection;
import java.util.Optional;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Database {
    protected final JavaPlugin plugin;
    protected String connectionUrl;
    protected Class<? extends TableCreator>[] tableCreators;

    @SafeVarargs
    public Database(JavaPlugin plugin, Class<? extends TableCreator>... tableCreators) {
        this.plugin = plugin;
        this.tableCreators = tableCreators;
    }

    public abstract Optional<Connection> getConnection();

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public abstract void createTables();
}
