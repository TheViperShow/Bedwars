package me.thevipershow.aussiebedwars;

import me.thevipershow.aussiebedwars.bedwars.spawner.SpawnerLevel;
import me.thevipershow.aussiebedwars.commands.CommandsManager;
import me.thevipershow.aussiebedwars.config.BedwarsGamemodeConfig;
import me.thevipershow.aussiebedwars.config.ConfigManager;
import me.thevipershow.aussiebedwars.config.DefaultConfiguration;
import me.thevipershow.aussiebedwars.config.DuoConfig;
import me.thevipershow.aussiebedwars.config.SoloConfig;
import me.thevipershow.aussiebedwars.config.objects.DuoBedwars;
import me.thevipershow.aussiebedwars.config.objects.Enchantment;
import me.thevipershow.aussiebedwars.config.objects.Merchant;
import me.thevipershow.aussiebedwars.config.objects.Shop;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import me.thevipershow.aussiebedwars.config.objects.SoloBedwars;
import me.thevipershow.aussiebedwars.config.objects.Spawner;
import me.thevipershow.aussiebedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.aussiebedwars.config.objects.UpgradeItem;
import me.thevipershow.aussiebedwars.config.objects.UpgradeLevel;
import me.thevipershow.aussiebedwars.game.GameManager;
import me.thevipershow.aussiebedwars.listeners.queue.MatchmakingVillagersListener;
import me.thevipershow.aussiebedwars.listeners.queue.QueueResizerListener;
import me.thevipershow.aussiebedwars.storage.sql.Database;
import me.thevipershow.aussiebedwars.storage.sql.MySQLDatabase;
import me.thevipershow.aussiebedwars.worlds.WorldsManager;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class AussieBedwars extends JavaPlugin {

    public static String PREFIX = "§7[§eAussieBedwars§7]: ";
    public static String MYSQL_DRIVER_CLASS = null;

    private WorldsManager worldsManager;
    private CommandsManager commandsManager;
    private GameManager gameManager;
    private Database database;
    private DefaultConfiguration defaultConfiguration;
    // loading file configurations:
    private BedwarsGamemodeConfig<SoloBedwars> soloConfig;
    private BedwarsGamemodeConfig<DuoBedwars> duoConfig;
    private ConfigManager configManager;

    // Global Listeners section
    private Listener matchmakingVillagerListener;
    private Listener queueResizerListener;

    static {
        try {
            MYSQL_DRIVER_CLASS = Class.forName("com.mysql.jdbc.Driver").getCanonicalName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void registerSerializers() {
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
    }

//
//         _nnnn_
//        dGGGGMMb     ,"""""""""""""".
//       @p~qp~~qMb    | Linux Rules! |
//       M|@||@) M|   _;..............'
//       @,----.JM| -'
//      JS^\__/  qKL
//     dZP        qKRb
//    dZP          qKKb
//   fZP            SMMb
//   HZM            MMMM
//   FqM            MMMM
// __| ".        |\dS"qML
// |    `.       | `' \Zq
//_)      \.___.,|     .'
//\____   )MMMMMM|   .'
//     `-'       `--'


    @Override
    public void onLoad() {
        registerSerializers();
    }

    @Override
    public void onEnable() { // Plugin startup logic
        ScoreboardLib.setPluginInstance(this);
        defaultConfiguration = new DefaultConfiguration(this);
        soloConfig = new SoloConfig(this);
        soloConfig.saveDefaultConfig();

        duoConfig = new DuoConfig(this);
        duoConfig.saveDefaultConfig();

        configManager = new ConfigManager(defaultConfiguration, soloConfig, duoConfig); // TODO: add extras

        worldsManager = WorldsManager.getInstanceSafe(configManager, this);
        worldsManager.cleanPreviousDirs();

        commandsManager = CommandsManager.getInstance(this);
        commandsManager.registerAll();

        database = new MySQLDatabase(this, defaultConfiguration);
        gameManager = new GameManager(this, worldsManager, soloConfig, duoConfig);
        gameManager.loadBaseAmount();

        matchmakingVillagerListener = new MatchmakingVillagersListener(this, gameManager);
        getServer().getPluginManager().registerEvents(matchmakingVillagerListener, this);
        queueResizerListener = new QueueResizerListener(gameManager);
        getServer().getPluginManager().registerEvents(queueResizerListener, this);
    }

    @Override
    public void onDisable() {
        
        worldsManager.getActiveGameList().forEach(game -> getServer().unloadWorld(game.getAssociatedWorld(), false));
    }

}
