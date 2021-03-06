package me.thevipershow.bedwars.worlds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.LoggerUtils;
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
        if (filez.length == 0) {
            return;
        }
        for (BedwarsGame bedwarsGame : createdAmountsMap.keySet()) {
            for (File file : filez) {
                if (file.getName().contains(bedwarsGame.getMapFilename())) {
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean cleanWorldUUIDs(File worldFolder) {
        if (!worldFolder.exists())
            throw new RuntimeException("Map File source did not exist?");
        if (!worldFolder.isDirectory())
            throw new RuntimeException("Map File source was not a folder!");
        for (final File file : worldFolder.listFiles()) {
            if (file.getName().equals("uid.dat")) {
                file.delete();
                return true;
            }
        }
        return false;
    }

    public final void load(BedwarsGame game) {
        final Logger log = plugin.getLogger();
        final String fileName = game.getMapFilename();
        final Integer i = createdAmountsMap.get(game);
        final int currentInt = (i == null ? 1 : i + 1);
        final String tempName = fileName + "-" + currentInt;

        if (plugin.getServer().getWorlds().stream().anyMatch(w -> w.getName().equals(tempName))) {
            throw new RuntimeException("The plugin tried to load a world that already existed and was loaded -> " + tempName);
        }

        final File sourceFile = new File(game.getConfigurationFolder(), game.getMapFilename());

        if (cleanWorldUUIDs(sourceFile)) {
            LoggerUtils.logColor(log, "Bedwars has deleted uid.dat from " + game.getMapFilename() + " in order to prevent future copying issues!");
        }

        if (!sourceFile.exists()) {
            LoggerUtils.logColor(log, String.format(AllStrings.COULD_NOT_FIND_WORLD_FOLDER.get(),  sourceFile.getAbsolutePath()));
            return;
        }

        final File outputFile = new File(worldContainer.getAbsolutePath(), tempName);

        final WorldLoader worldLoader = new WorldLoader(sourceFile, outputFile);
        LoggerUtils.logColor(log, String.format(AllStrings.ATTEMPT_COPY.get(), tempName));
        final boolean copyResult = worldLoader.copyToDir();

        LoggerUtils.logColor(log, String.format(AllStrings.ATTEMPT_CREATE.get(), tempName));

        World w = Objects.requireNonNull(
                WorldCreator.name(tempName)
                        .environment(World.Environment.NORMAL)
                        .generateStructures(false)
                        .type(WorldType.CUSTOMIZED)
                        .createWorld(),
                "Something has went wrong during World creation for " + tempName);

        LoggerUtils.logColor(log, String.format(AllStrings.LOADING_ACTIVE_GAME.get(), tempName));

        final ActiveGame activeGame = GameUtils.from(w, game, lobbyWorld, plugin);

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

        activeGame.initialize(); // Initializing the game!
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

    public HashMap<BedwarsGame, Integer> getCreatedAmountsMap() {
        return createdAmountsMap;
    }
}
