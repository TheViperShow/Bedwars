package me.thevipershow.bedwars;

import me.thevipershow.bedwars.bedwars.spawner.SpawnerLevel;
import me.thevipershow.bedwars.commands.BedwarsMainCommand;
import me.thevipershow.bedwars.config.BedwarsGamemodeConfig;
import me.thevipershow.bedwars.config.ConfigManager;
import me.thevipershow.bedwars.config.DefaultConfiguration;
import me.thevipershow.bedwars.config.DuoConfig;
import me.thevipershow.bedwars.config.QuadConfig;
import me.thevipershow.bedwars.config.SoloConfig;
import me.thevipershow.bedwars.config.objects.DuoBedwars;
import me.thevipershow.bedwars.config.objects.Enchantment;
import me.thevipershow.bedwars.config.objects.Merchant;
import me.thevipershow.bedwars.config.objects.QuadBedwars;
import me.thevipershow.bedwars.config.objects.Shop;
import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.SoloBedwars;
import me.thevipershow.bedwars.config.objects.Spawner;
import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.bedwars.config.objects.UpgradeItem;
import me.thevipershow.bedwars.config.objects.UpgradeLevel;
import me.thevipershow.bedwars.config.objects.upgradeshop.DragonBuffUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.HealPoolUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.IronForgeUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.ManiacMinerUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.ReinforcedArmorUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.SharpnessUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.TrapUpgrades;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShop;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.AlarmTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.BlindnessAndPoisonTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.CounterOffensiveTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.MinerFatigueTrap;
import me.thevipershow.bedwars.game.GameManager;
import me.thevipershow.bedwars.listeners.LevelUpListener;
import me.thevipershow.bedwars.listeners.queue.MatchmakingVillagersListener;
import me.thevipershow.bedwars.listeners.queue.QueueResizerListener;
import me.thevipershow.bedwars.placeholders.BedwarsExpansion;
import me.thevipershow.bedwars.storage.sql.DataCleaner;
import me.thevipershow.bedwars.storage.sql.Database;
import me.thevipershow.bedwars.storage.sql.MySQLDatabase;
import me.thevipershow.bedwars.worlds.WorldsManager;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Bedwars extends JavaPlugin {

    public static Plugin plugin = null;

    public static String PREFIX = AllStrings.PREFIX.get();
    public static String MYSQL_DRIVER_CLASS = null;

    private WorldsManager worldsManager = null;
    private GameManager gameManager = null;
    private Database database = null;
    private DefaultConfiguration defaultConfiguration = null;
    // loading file configurations:
    private BedwarsGamemodeConfig<SoloBedwars> soloConfig = null;
    private BedwarsGamemodeConfig<DuoBedwars> duoConfig = null;
    private BedwarsGamemodeConfig<QuadBedwars> quadConfig = null;
    private ConfigManager configManager = null;

    // Global Listeners section
    private Listener matchmakingVillagerListener = null;
    private Listener queueResizerListener = null;
    private Listener levelUpListener = null;

    // Cleaners
    private DataCleaner dataCleaner = null;

    static {   // Loading MYSQL Drivers for the plugin
        try {
            MYSQL_DRIVER_CLASS = Class.forName(AllStrings.DRIVER_PATH.get()).getCanonicalName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void registerSerializers() {
        ConfigurationSerialization.registerClass(DragonBuffUpgrade.class);
        ConfigurationSerialization.registerClass(HealPoolUpgrade.class);
        ConfigurationSerialization.registerClass(IronForgeUpgrade.class);
        ConfigurationSerialization.registerClass(ManiacMinerUpgrade.class);
        ConfigurationSerialization.registerClass(ReinforcedArmorUpgrade.class);
        ConfigurationSerialization.registerClass(SharpnessUpgrade.class);
        ConfigurationSerialization.registerClass(UpgradeShop.class);
        ConfigurationSerialization.registerClass(Enchantment.class);
        ConfigurationSerialization.registerClass(UpgradeItem.class);
        ConfigurationSerialization.registerClass(UpgradeLevel.class);
        ConfigurationSerialization.registerClass(SpawnerLevel.class);
        ConfigurationSerialization.registerClass(Spawner.class);
        ConfigurationSerialization.registerClass(ShopItem.class);
        ConfigurationSerialization.registerClass(Shop.class);
        ConfigurationSerialization.registerClass(Merchant.class);
        ConfigurationSerialization.registerClass(SoloBedwars.class);
        ConfigurationSerialization.registerClass(ShopItem.class);
        ConfigurationSerialization.registerClass(TeamSpawnPosition.class);
        ConfigurationSerialization.registerClass(DuoBedwars.class);
        ConfigurationSerialization.registerClass(MinerFatigueTrap.class);
        ConfigurationSerialization.registerClass(BlindnessAndPoisonTrap.class);
        ConfigurationSerialization.registerClass(CounterOffensiveTrap.class);
        ConfigurationSerialization.registerClass(AlarmTrap.class);
        ConfigurationSerialization.registerClass(TrapUpgrades.class);
        ConfigurationSerialization.registerClass(QuadBedwars.class);
    }

    @Override
    public final void onLoad() {
        registerSerializers();
    }

    @Override
    public final void onEnable() { // Plugin startup logic
        plugin = this;
        ScoreboardLib.setPluginInstance(this);                       // Setting the plugin instance for the ScoreboardLib
        defaultConfiguration = new DefaultConfiguration(this); // Instantiating new DefaultConfiguration

        soloConfig = new SoloConfig(this); // Instantiating all configs:
        soloConfig.saveDefaultConfig(); //saving them

        duoConfig = new DuoConfig(this);
        duoConfig.saveDefaultConfig();

        quadConfig = new QuadConfig(this);
        quadConfig.saveDefaultConfig();

        configManager = new ConfigManager(defaultConfiguration, soloConfig, duoConfig, quadConfig);

        worldsManager = WorldsManager.getInstanceSafe(configManager, this);
        worldsManager.cleanPreviousDirs();

        database = new MySQLDatabase(this, defaultConfiguration);
        gameManager = new GameManager(this, worldsManager, soloConfig, duoConfig, quadConfig);
        gameManager.loadBaseAmount();

        final PluginCommand pluginCommand = getServer().getPluginCommand(AllStrings.MAIN_COMMAND.get());
        final BedwarsMainCommand aussieBedwarsMainCommand = new BedwarsMainCommand(this, gameManager);
        pluginCommand.setExecutor(aussieBedwarsMainCommand);
        pluginCommand.setTabCompleter(aussieBedwarsMainCommand);

        matchmakingVillagerListener = new MatchmakingVillagersListener(this, gameManager); // Insantiating listeners
        queueResizerListener = new QueueResizerListener(gameManager);
        levelUpListener = new LevelUpListener();
        getServer().getPluginManager().registerEvents(matchmakingVillagerListener, this); // Registering global listeners
        getServer().getPluginManager().registerEvents(queueResizerListener, this);
        getServer().getPluginManager().registerEvents(levelUpListener, this);

        dataCleaner = new DataCleaner(this);
        dataCleaner.startTasks();

        registerExpansions();
    }

    private void registerExpansions() {
        if (getServer().getPluginManager().getPlugin(AllStrings.PAPI_PLUGIN.get()) != null) {
            final BedwarsExpansion bedwarsExpansion = new BedwarsExpansion(gameManager);
            if (bedwarsExpansion.register()) {
                LoggerUtils.logColor(getLogger(), AllStrings.SUCCESSFULLY_ADDED_PAPI_EXPANSION.get());
            }
        }
    }

    @Override
    public final void onDisable() {
        dataCleaner.stopTasks();
        worldsManager.getActiveGameList().forEach(game -> getServer().unloadWorld(game.getAssociatedWorld(), false));
    }

}