package me.thevipershow.aussiebedwars;

import java.util.Objects;
import me.thevipershow.aussiebedwars.bedwars.spawner.SpawnerLevel;
import me.thevipershow.aussiebedwars.commands.CommandsManager;
import me.thevipershow.aussiebedwars.config.SoloConfig;
import me.thevipershow.aussiebedwars.config.objects.Merchant;
import me.thevipershow.aussiebedwars.config.objects.Shop;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import me.thevipershow.aussiebedwars.config.objects.SoloBedwars;
import me.thevipershow.aussiebedwars.config.objects.Spawner;
import me.thevipershow.aussiebedwars.worlds.WorldsManager;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public final class AussieBedwars extends JavaPlugin {

    public static String PREFIX = "§7[§eAussieBedwars§7]: ";

    private SoloConfig soloConfig;
    private WorldsManager worldsManager;
    private CommandsManager commandsManager;

    private static void registerSerializers() {
        ConfigurationSerialization.registerClass(SpawnerLevel.class);
        ConfigurationSerialization.registerClass(Spawner.class);
        ConfigurationSerialization.registerClass(ShopItem.class);
        ConfigurationSerialization.registerClass(Shop.class);
        ConfigurationSerialization.registerClass(Merchant.class);
        ConfigurationSerialization.registerClass(SoloBedwars.class);
        ConfigurationSerialization.registerClass(ShopItem.class);
    }

    @Override
    public void onEnable() { // Plugin startup logic
        saveDefaultConfig();
        soloConfig = new SoloConfig(this);
        soloConfig.saveDefaultConfig();

        worldsManager = WorldsManager.getInstance(this, Objects.requireNonNull(soloConfig, "Config didn't load correctly!"));
        worldsManager.loadBaseAmount();

        commandsManager = CommandsManager.getInstance(this);
        commandsManager.registerAll();
    }

    @Override
    public void onDisable() {

    }
}
