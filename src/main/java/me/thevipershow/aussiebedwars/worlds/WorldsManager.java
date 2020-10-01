package me.thevipershow.aussiebedwars.worlds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import me.thevipershow.aussiebedwars.LoggerUtils;
import me.thevipershow.aussiebedwars.config.BedwarsGamemodeConfig;
import me.thevipershow.aussiebedwars.config.ConfigManager;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GamemodeUtilities;
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
            LoggerUtils.logColor(log, String.format("&cCould not find a world folder named &f[&e%s&f]",  sourceFile.getAbsolutePath()));
            return;
        }

        final File outputFile = new File(worldContainer.getAbsolutePath(), tempName);

        final WorldLoader worldLoader = new WorldLoader(sourceFile, outputFile);
        LoggerUtils.logColor(log, String.format("&3Attempting to copy directory of Bukkit World &f[&e%s&f].", tempName));
        final boolean copyResult = worldLoader.copyToDir();

        LoggerUtils.logColor(log, String.format("&3Attempting to create instance of Bukkit World &f[&e%s&f].", tempName));

        final World w = WorldCreator.name(tempName)
                .environment(World.Environment.NORMAL)
                .generateStructures(false)
                .type(WorldType.CUSTOMIZED)
                .createWorld();

        LoggerUtils.logColor(log, String.format("&3Loading &f[&e%s&f] &3into active games...", tempName));

        final ActiveGame activeGame = GamemodeUtilities.fromGamemode(tempName, game, lobbyWorld, plugin);

        if (w != null && copyResult) {
            LoggerUtils.logColor(log, String.format("&3Successfully created a world with name &f[&e%s&f].", tempName));
            createdAmountsMap.put(game, currentInt);
            if (activeGame == null) {
                LoggerUtils.logColor(log, String.format("&cCould not create ActiveGame for &f[&e%s&f]", tempName));
            } else {
                LoggerUtils.logColor(log, "&3Added new ActiveGame &f[&e" + tempName + "&f].");
                activeGameSet.add(activeGame);
            }
        } else {
            LoggerUtils.logColor(log, String.format("&cSomething went wrong when creating world &f[&e%s&f].", tempName));
        }

    }

    public World getLobbyWorld() {
        return lobbyWorld;
    }

    public List<ActiveGame> getActiveGameList() {
        return activeGameSet;
    }

    public HashMap<BedwarsGame, Integer> getCreatedAmountsMap() {
        return createdAmountsMap;
    }
}
