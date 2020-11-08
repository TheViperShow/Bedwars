package me.thevipershow.bedwars.worlds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.LoggerUtils;
import me.thevipershow.bedwars.config.BedwarsGamemodeConfig;
import me.thevipershow.bedwars.config.ConfigManager;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldsManager {

    private static WorldsManager instance = null;

    public static WorldsManager getInstanceSafe(final ConfigManager configManager, final JavaPlugin plugin) {
        if (instance == null) {
            instance = new WorldsManager(configManager, plugin);
        }
        return instance;
    }

    public static WorldsManager getInstanceUnsafe() {
        return instance;
    }

    private final ConfigManager configManager;
    private final JavaPlugin plugin;
    private final File worldContainer;
    private final File pluginFolder;
    private final World lobbyWorld;
    private final HashMap<BedwarsGame, Integer> createdAmountsMap;
    private final List<ActiveGame> activeGameSet;

    private WorldsManager(final ConfigManager configManager, final JavaPlugin plugin) {
        this.configManager = configManager;
        this.plugin = plugin;
        this.lobbyWorld = Bukkit.getWorld(configManager.getDefaultConfiguration().getLobbyName());
        this.worldContainer = plugin.getServer().getWorldContainer();
        this.pluginFolder = plugin.getDataFolder();
        this.activeGameSet = new ArrayList<>();
        this.createdAmountsMap = new HashMap<>();
    }

    public final void cleanPreviousDirs() {
        final File[] filez = worldContainer.listFiles();
        assert filez != null;
        if (filez.length == 0) return;
        for (final BedwarsGamemodeConfig<? extends BedwarsGame> config : configManager.getConfigs()) {
            for (final BedwarsGame game : config.getBedwarsObjects()) {
                for (final File file : filez) {
                    if (file.getName().contains(game.getMapFilename())) {
                        try {
                            FileUtils.deleteDirectory(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public final void load(final BedwarsGame game) {
        final Logger log = plugin.getLogger();
        final String fileName = game.getMapFilename();
        final Integer i = createdAmountsMap.get(game);
        final int currentInt = i == null ? 1 : i + 1;
        final String tempName = fileName + "-" + currentInt;
        final File sourceFile = new File(pluginFolder.getAbsolutePath(), game.getMapFilename());

        if (!sourceFile.exists()) {
            LoggerUtils.logColor(log, String.format(AllStrings.COULD_NOT_FIND_WORLD_FOLDER.get(),  sourceFile.getAbsolutePath()));
            return;
        }

        final File outputFile = new File(worldContainer.getAbsolutePath(), tempName);

        final WorldLoader worldLoader = new WorldLoader(sourceFile, outputFile);
        LoggerUtils.logColor(log, String.format(AllStrings.ATTEMPT_COPY.get(), tempName));
        final boolean copyResult = worldLoader.copyToDir();

        LoggerUtils.logColor(log, String.format(AllStrings.ATTEMPT_CREATE.get(), tempName));

        final World w = WorldCreator.name(tempName)
                .environment(World.Environment.NORMAL)
                .generateStructures(false)
                .type(WorldType.CUSTOMIZED)
                .createWorld();

        LoggerUtils.logColor(log, String.format(AllStrings.LOADING_ACTIVE_GAME.get(), tempName));

        final ActiveGame activeGame = GameUtils.from(tempName, game, lobbyWorld, plugin);

        if (w != null && copyResult) {
            LoggerUtils.logColor(log, String.format(AllStrings.SUCCESSFULLY_CREATED_ACTIVE_GAME.get(), tempName));
            createdAmountsMap.put(game, currentInt);
            if (activeGame == null) {
                LoggerUtils.logColor(log, String.format(AllStrings.ERROR_CREATE_ACTIVE_GAME.get(), tempName));
            } else {
                LoggerUtils.logColor(log,  AllStrings.ADDED_NEW_ACTIVE_GAME.get() + tempName + "&f].");
                activeGameSet.add(activeGame);
            }
        } else {
            LoggerUtils.logColor(log, String.format(AllStrings.SOMETHING_WENT_WRONG_DURING_CREATION.get(), tempName));
        }

    }

    public World getLobbyWorld() {
        return lobbyWorld;
    }

    public List<ActiveGame> getActiveGameList() {
        return activeGameSet;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public File getWorldContainer() {
        return worldContainer;
    }

    public File getPluginFolder() {
        return pluginFolder;
    }

    public List<ActiveGame> getActiveGameSet() {
        return activeGameSet;
    }

    public HashMap<BedwarsGame, Integer> getCreatedAmountsMap() {
        return createdAmountsMap;
    }
}
