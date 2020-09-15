package me.thevipershow.aussiebedwars.worlds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        final String fileName = game.getMapFilename();
        final Integer i = createdAmountsMap.get(game);
        final int currentInt = i == null ? 1 : i + 1;
        final String tempName = fileName + "-" + currentInt;
        final File sourceFile = new File(pluginFolder.getAbsolutePath(), game.getMapFilename());

        if (!sourceFile.exists()) {
            plugin.getLogger().warning(String.format("Could not find a world folder named [%s].", sourceFile.getAbsolutePath()));
            return;
        }

        final File outputFile = new File(worldContainer.getAbsolutePath(), tempName);

        final WorldLoader worldLoader = new WorldLoader(sourceFile, outputFile);
        plugin.getLogger().info(String.format("Attempting to copy directory of bukkit World [%s].", tempName));
        final boolean copyResult = worldLoader.copyToDir();
        plugin.getLogger().info(String.format("loaded world [%s].", tempName));

        plugin.getLogger().info(String.format("Attempting to create instance of bukkit World [%s].", tempName));

        plugin.getLogger().info("Copied status: " + copyResult);
        final World w = WorldCreator.name(tempName)
                .environment(World.Environment.NORMAL)
                .generateStructures(false)
                .type(WorldType.CUSTOMIZED)
                .createWorld();

        plugin.getLogger().info("LOADED WORLD -> " + w.toString());

        plugin.getLogger().info(String.format("Loading [%s] into active games...", tempName));

        final ActiveGame activeGame = GamemodeUtilities.fromGamemode(tempName, game, lobbyWorld, plugin);

        if (w != null && copyResult) {
            plugin.getLogger().info(String.format("Successfully created a world name [%s].", tempName));
            createdAmountsMap.put(game, currentInt);
            if (activeGame == null) {
                plugin.getLogger().severe(String.format("Could not create active game for [%s].", tempName));
            } else {
                plugin.getLogger().info("Added new ActiveGame [" + tempName + "].");
                activeGameSet.add(activeGame);
            }
        } else {
            plugin.getLogger().warning(String.format("Something went wrong when creating world [%s].", tempName));
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
