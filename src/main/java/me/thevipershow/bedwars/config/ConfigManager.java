package me.thevipershow.bedwars.config;

import me.thevipershow.bedwars.config.objects.BedwarsGame;

public final class ConfigManager {
    private final DefaultConfiguration defaultConfiguration;
    private final BedwarsGamemodeConfig<? extends BedwarsGame>[] configs;

    public ConfigManager(final DefaultConfiguration defaultConfiguration, final BedwarsGamemodeConfig<? extends BedwarsGame>... configs) {
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
