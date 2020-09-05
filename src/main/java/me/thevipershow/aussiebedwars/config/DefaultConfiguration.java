package me.thevipershow.aussiebedwars.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultConfiguration {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public DefaultConfiguration(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setValues();
    }

    private void setValues() {

    }
}
