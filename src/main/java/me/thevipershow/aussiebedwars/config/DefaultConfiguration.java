package me.thevipershow.aussiebedwars.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultConfiguration {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    private String databaseUsername = null;
    private String password = null;
    private String address = null;
    private Integer port = null;
    private String databaseName = null;

    private String lobbyName = null;

    public DefaultConfiguration(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setValues();
    }

    private void setValues() {
        plugin.reloadConfig();
        this.databaseUsername = config.getString("settings.database.username");
        this.password = config.getString("settings.database.password");
        this.address = config.getString("settings.database.address");
        this.port = config.getInt("settings.database.port");
        this.databaseName = config.getString("settings.database.db-name");
        this.lobbyName = config.getString("settings.lobby.world-name");
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPort() {
        return port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getLobbyName() {
        return lobbyName;
    }
}
