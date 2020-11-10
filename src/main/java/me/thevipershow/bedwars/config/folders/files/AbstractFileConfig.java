package me.thevipershow.bedwars.config.folders.files;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import me.thevipershow.bedwars.config.folders.ConfigFiles;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class AbstractFileConfig {

    public static Map<String, Object> removeMemorySections(final Map<String, Object> map) {
        final Map<String, Object> newMap = new HashMap<>();
        map.forEach((k,v) -> {
            if (v instanceof MemorySection) {
                v = ((MemorySection) v).getValues(false);
            }
            newMap.put(k, v);
        });
        return newMap;
    }

    public static Map<String, Object> removeMemorySections(final Object object) {
        if (!(object instanceof MemorySection)) {
            throw new IllegalArgumentException(String.format("Object is not a memory section (%s)", object.getClass().getSimpleName()));
        } else {
            return removeMemorySections(((MemorySection) object).getValues(false));
        }
    }

    public final Map<String, Object> getMap(final String key) {
        return removeMemorySections(this.config.getConfigurationSection(key).getValues(false));
    }

    protected final File file;
    protected final FileConfiguration config;
    protected final ConfigFiles configFiles;

    public AbstractFileConfig(File file, ConfigFiles configFiles) {
        this.file = file;
        this.configFiles = configFiles;
        this.config = new YamlConfiguration();
        try {
            this.config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfiguration() {
        return config;
    }

    public ConfigFiles getConfigFiles() {
        return configFiles;
    }
}
