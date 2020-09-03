package me.thevipershow.aussiebedwars.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public final class MainConfiguration extends ConfigValues {
    public MainConfiguration(Plugin plugin, FileConfiguration fileConfiguration) {
        super(plugin, fileConfiguration);
    }

    @Override
    public void updateValues() {
        plugin.reloadConfig();
    }
}
