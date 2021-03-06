package me.thevipershow.bedwars.config;

import java.util.List;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.Gamemode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultConfiguration {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public static String serverName = null;
    public static String serverDomain = null;
    private String databaseUsername = null;
    private String password = null;
    private String address = null;
    private Integer port = null;
    private String databaseName = null;
    private List<Gamemode> attemptLoad = null;

    private String lobbyName = null;

    public DefaultConfiguration(final JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setValues();
    }

    private void setValues() {
        plugin.reloadConfig();
        serverName = config.getString(AllStrings.SERVER_NAME.get());
        serverDomain = config.getString(AllStrings.SERVER_DOMAIN.get());
        AllStrings.PREFIX.setS(String.format("&8[&e%s&8]&7: ", serverName));
        AllStrings.SERVER_BRAND.setS(serverDomain);
        this.databaseUsername = config.getString(AllStrings.DB_USER.get());
        this.password = config.getString(AllStrings.DB_PASSWORD.get());
        this.address = config.getString(AllStrings.DB_ADDRESS.get());
        this.port = config.getInt(AllStrings.DB_PORT.get());
        this.databaseName = config.getString(AllStrings.DB_NAME.get());
        this.lobbyName = config.getString(AllStrings.LOBBY_NAME.get());
        this.attemptLoad = config.getStringList(AllStrings.ATTEMPT_LOAD.get()).stream().map(s -> Gamemode.valueOf(s.toUpperCase())).collect(Collectors.toList());
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

    public List<Gamemode> getAttemptLoad() {
        return attemptLoad;
    }
}
