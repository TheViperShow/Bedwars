package me.thevipershow.bedwars.config;

import me.thevipershow.bedwars.AllStrings;
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
        this.databaseUsername = config.getString(AllStrings.DB_USER.get());
        this.password = config.getString(AllStrings.DB_PASSWORD.get());
        this.address = config.getString(AllStrings.DB_ADDRESS.get());
        this.port = config.getInt(AllStrings.DB_PORT.get());
        this.databaseName = config.getString(AllStrings.DB_NAME.get());
        this.lobbyName = config.getString(AllStrings.LOBBY_NAME.get());
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
