package me.thevipershow.aussiebedwars;

import me.thevipershow.aussiebedwars.bedwars.spawner.Spawner;
import me.thevipershow.aussiebedwars.bedwars.spawner.SpawnerLevel;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public final class AussieBedwars extends JavaPlugin {

    @Override
    public void onEnable() { // Plugin startup logic
        ConfigurationSerialization.registerClass(SpawnerLevel.class);
        ConfigurationSerialization.registerClass(Spawner.class);
        saveDefaultConfig();
    }
}
