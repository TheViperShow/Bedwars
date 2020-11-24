package me.thevipershow.bedwars;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import me.thevipershow.bedwars.commands.BedwarsMainCommand;
import me.thevipershow.bedwars.config.ConfigManager;
import me.thevipershow.bedwars.config.DefaultConfiguration;
import me.thevipershow.bedwars.config.folders.BedwarsGameFactory;
import me.thevipershow.bedwars.config.folders.ConfigFiles;
import me.thevipershow.bedwars.config.folders.ValidFoldersDiscoverer;
import me.thevipershow.bedwars.game.managers.GameManager;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.listeners.global.ActiveGameTerminateListener;
import me.thevipershow.bedwars.listeners.global.LevelUpListener;
import me.thevipershow.bedwars.listeners.global.MatchmakingVillagersListener;
import me.thevipershow.bedwars.listeners.global.QueueResizerListener;
import me.thevipershow.bedwars.placeholders.BedwarsExpansion;
import me.thevipershow.bedwars.storage.sql.DataCleaner;
import me.thevipershow.bedwars.storage.sql.Database;
import me.thevipershow.bedwars.storage.sql.MySQLDatabase;
import me.thevipershow.bedwars.worlds.WorldsManager;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Bedwars extends JavaPlugin {

    public static Plugin plugin = null;

    public static String MYSQL_DRIVER_CLASS = null;

    private WorldsManager worldsManager = null;
    private GameManager gameManager = null;
    private Database database = null;
    private DefaultConfiguration defaultConfiguration = null;

    private ConfigManager configManager = null;
    private ValidFoldersDiscoverer validFoldersDiscoverer;
    private BedwarsGameFactory bedwarsGameFactory;

    // Global Listeners section
    private Listener matchmakingVillagerListener = null;
    private Listener queueResizerListener = null;
    private Listener levelUpListener = null;
    private Listener activeGameTerminateListener = null;

    // Cleaners
    private DataCleaner dataCleaner = null;

    static {   // Loading MYSQL Drivers for the plugin
        try {
            MYSQL_DRIVER_CLASS = Class.forName(AllStrings.DRIVER_PATH.get()).getCanonicalName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void exportSampleFolder() {
        try {
            File output = new File(getDataFolder(), "sample-game");
            if (!output.exists()) {
                output.mkdir();
            } else {
                return;
            }
            for (ConfigFiles value : ConfigFiles.values()) {
                File current = new File(output, value.getFilename());
                if (!current.exists()) {
                    current.createNewFile();
                }
                try (InputStream inputStream = getResource(value.getFilename())) {
                    FileUtils.copyInputStreamToFile(inputStream, current);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //    plugin.saveResource("sample-game" + File.separatorChar, false);
    }

    @Override
    public final void onLoad() {
        GameUtils.registerSerializers();
    }

    @Override
    public final void onEnable() { // Plugin startup logic
        plugin = this;
        ScoreboardLib.setPluginInstance(this);                       // Setting the plugin instance for the ScoreboardLib

        exportSampleFolder(); // saving sample-game folder if it didn't exist.

        validFoldersDiscoverer = new ValidFoldersDiscoverer(this);
        bedwarsGameFactory = new BedwarsGameFactory(validFoldersDiscoverer);

        defaultConfiguration = new DefaultConfiguration(this); // Instantiating new DefaultConfiguration

        configManager = new ConfigManager(defaultConfiguration);

        worldsManager = WorldsManager.getInstanceSafe(configManager, this);
        worldsManager.cleanPreviousDirs();

        database = new MySQLDatabase(this, defaultConfiguration);
        gameManager = new GameManager(this, worldsManager, bedwarsGameFactory);
        gameManager.loadBaseAmount();

        final PluginCommand pluginCommand = getServer().getPluginCommand(AllStrings.MAIN_COMMAND.get());
        final BedwarsMainCommand aussieBedwarsMainCommand = new BedwarsMainCommand(this, gameManager);
        pluginCommand.setExecutor(aussieBedwarsMainCommand);
        pluginCommand.setTabCompleter(aussieBedwarsMainCommand);

        matchmakingVillagerListener = new MatchmakingVillagersListener(this, gameManager); // Insantiating listeners
        queueResizerListener = new QueueResizerListener(gameManager);
        levelUpListener = new LevelUpListener();
        activeGameTerminateListener = new ActiveGameTerminateListener(worldsManager);
        getServer().getPluginManager().registerEvents(matchmakingVillagerListener, this); // Registering global listeners
        getServer().getPluginManager().registerEvents(queueResizerListener, this);
        getServer().getPluginManager().registerEvents(levelUpListener, this);
        getServer().getPluginManager().registerEvents(activeGameTerminateListener, this);

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
        worldsManager.getActiveGameList().forEach(game -> getServer().unloadWorld(game.getCachedGameData().getGame(), false));
    }

}
