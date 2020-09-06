package me.thevipershow.aussiebedwars;

import java.sql.Driver;
import me.thevipershow.aussiebedwars.bedwars.spawner.SpawnerLevel;
import me.thevipershow.aussiebedwars.commands.CommandsManager;
import me.thevipershow.aussiebedwars.config.BedwarsGamemodeConfig;
import me.thevipershow.aussiebedwars.config.ConfigManager;
import me.thevipershow.aussiebedwars.config.DefaultConfiguration;
import me.thevipershow.aussiebedwars.config.SoloConfig;
import me.thevipershow.aussiebedwars.config.objects.Merchant;
import me.thevipershow.aussiebedwars.config.objects.Shop;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import me.thevipershow.aussiebedwars.config.objects.SoloBedwars;
import me.thevipershow.aussiebedwars.config.objects.Spawner;
import me.thevipershow.aussiebedwars.game.GameManager;
import me.thevipershow.aussiebedwars.storage.sql.Database;
import me.thevipershow.aussiebedwars.storage.sql.MySQLDatabase;
import me.thevipershow.aussiebedwars.worlds.WorldsManager;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public final class AussieBedwars extends JavaPlugin {

    public static String PREFIX = "§7[§eAussieBedwars§7]: ";
    public static Class<? extends Driver> MYSQL_DRIVER_CLASS = null;

    private WorldsManager worldsManager;
    private CommandsManager commandsManager;
    private GameManager gameManager;
    private Database database;
    private DefaultConfiguration defaultConfiguration;
    // loading file configurations:
    private BedwarsGamemodeConfig<SoloBedwars> soloConfig;
    private ConfigManager configManager;

    private static void registerDriver() {
        try {
            MYSQL_DRIVER_CLASS = (Class<? extends Driver>) Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

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
        registerDriver();
        defaultConfiguration = new DefaultConfiguration(this);
        soloConfig = new SoloConfig(this);
        soloConfig.saveDefaultConfig();

        configManager = new ConfigManager(defaultConfiguration, soloConfig);

        worldsManager = new WorldsManager(configManager, this); //TODO: ADD OTHER GAMEMODES CONFIG:

        commandsManager = CommandsManager.getInstance(this);
        commandsManager.registerAll();

        database = new MySQLDatabase(this, defaultConfiguration);
        gameManager = new GameManager(this, worldsManager, soloConfig);
        gameManager.loadBaseAmount();
    }


    @Override
    public void onDisable() {

    }
}
