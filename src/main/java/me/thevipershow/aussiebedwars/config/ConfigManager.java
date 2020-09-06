package me.thevipershow.aussiebedwars.config;

import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;

public final class ConfigManager {
    private final DefaultConfiguration defaultConfiguration;
    private final BedwarsGamemodeConfig<? extends BedwarsGame>[] configs;

    public ConfigManager(DefaultConfiguration defaultConfiguration, BedwarsGamemodeConfig<? extends BedwarsGame>... configs) {
        this.defaultConfiguration = defaultConfiguration;
        this.configs = configs;
    }

    public BedwarsGamemodeConfig<? extends BedwarsGame>[] getConfigs() {
        return configs;
    }

    public DefaultConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }
}
