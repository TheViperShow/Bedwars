package me.thevipershow.aussiebedwars.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public abstract class ConfigValues {

    protected final Plugin plugin;
    protected final FileConfiguration fileConfiguration;

    public ConfigValues(Plugin plugin, FileConfiguration fileConfiguration) {
        this.plugin = plugin;
        this.fileConfiguration = fileConfiguration;
    }

    public abstract void updateValues();
}
