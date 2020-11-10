package me.thevipershow.bedwars.config;

public final class ConfigManager {
    private final DefaultConfiguration defaultConfiguration;

    public ConfigManager(final DefaultConfiguration defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;

    }

    public DefaultConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }
}
