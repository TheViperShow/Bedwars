package me.thevipershow.aussiebedwars.storage.sql;

import org.bukkit.plugin.java.JavaPlugin;

public abstract  class AbstractSecureDatabase extends Database {
    private final String username;
    private final String password;
    private final String address;
    private final String databaseName;
    private final int port;

    private AbstractSecureDatabase(JavaPlugin plugin,
                                  String username,
                                  String password,
                                  String address,
                                  String databaseName,
                                  int port,
                                  Class<? extends TableCreator>... tableCreators) {
        super(plugin, tableCreators);
        this.username = username;
        this.password = password;
        this.address = address;
        this.databaseName = databaseName;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public int getPort() {
        return port;
    }
}
