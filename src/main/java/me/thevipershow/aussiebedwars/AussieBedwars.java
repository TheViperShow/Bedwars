package me.thevipershow.aussiebedwars;

import me.thevipershow.aussiebedwars.bedwars.SoloBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.shops.Merchant;
import me.thevipershow.aussiebedwars.bedwars.objects.shops.Shop;
import me.thevipershow.aussiebedwars.bedwars.objects.shops.ShopItem;
import me.thevipershow.aussiebedwars.bedwars.spawner.Spawner;
import me.thevipershow.aussiebedwars.bedwars.spawner.SpawnerLevel;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public final class AussieBedwars extends JavaPlugin {

    private static void registerSerializers() {
        ConfigurationSerialization.registerClass(SpawnerLevel.class);
        ConfigurationSerialization.registerClass(Spawner.class);
        ConfigurationSerialization.registerClass(ShopItem.class);
        ConfigurationSerialization.registerClass(Shop.class);
        ConfigurationSerialization.registerClass(Merchant.class);
        ConfigurationSerialization.registerClass(SoloBedwars.class);
    }

    @Override
    public void onEnable() { // Plugin startup logic
        saveDefaultConfig();
    }
}
