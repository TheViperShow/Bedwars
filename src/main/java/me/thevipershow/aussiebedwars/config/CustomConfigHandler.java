package me.thevipershow.aussiebedwars.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomConfigHandler {
    private final JavaPlugin plugin;
    private final String fileName;
    private FileConfiguration customConfig = null;
    private File customConfigFile = null;

    public CustomConfigHandler(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.fileName = name;
        saveDefaultConfig();
        reloadConfig();
    }

    public boolean reloadConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(plugin.getDataFolder(), fileName);
        }
        customConfig = loadConfiguration(customConfigFile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        InputStream resource = plugin.getResource(fileName);
        if (resource != null) {
            defConfigStream = new InputStreamReader(resource, StandardCharsets.UTF_8);
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            customConfig.setDefaults(defConfig);
        }
        return true;
    }

    public File getFile() {
        return customConfigFile;
    }

    public FileConfiguration getConfig() {
        return customConfig;
    }

    public void saveConfig() {
        if (customConfig == null || customConfigFile == null) {
            return;
        }
        try {
            getConfig().save(customConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
        }
    }

    public void saveDefaultConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(plugin.getDataFolder(), fileName);
        }
        if (!customConfigFile.exists() && plugin.getResource(fileName) != null) {
            plugin.saveResource(fileName, false);
        }
    }

    public YamlConfiguration loadConfiguration(File file) {
        Validate.notNull(file, "File cannot be null");

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (FileNotFoundException e) {
            // empty
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Cannot load " + file, e);
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().log(Level.SEVERE, "Cannot load " + file, e);
        }
        return config;
    }
}
