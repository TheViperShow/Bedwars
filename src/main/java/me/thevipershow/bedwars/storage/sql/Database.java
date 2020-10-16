package me.thevipershow.bedwars.storage.sql;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class Database {
    protected final JavaPlugin plugin;
    public static String connectionUrl;
    protected Class<? extends TableCreator>[] tableCreators;

    @SafeVarargs
    public Database(final JavaPlugin plugin, final Class<? extends TableCreator>... tableCreators) {
        this.plugin = plugin;
        this.tableCreators = tableCreators;
    }

    public void setConnectionUrl(String connectionUrl) {
        Database.connectionUrl = connectionUrl;
    }

    public static String getConnectionUrl() {
        return connectionUrl;
    }

    public abstract void createTables();
}
