package me.thevipershow.aussiebedwars.worlds;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import me.thevipershow.aussiebedwars.config.ConfigManager;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameUtilities;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldsManager {

    private final ConfigManager configManager;
    private final JavaPlugin plugin;
    private final File worldContainer;
    private final File pluginFolder;
    private final Set<ActiveGame> activeGameSet;
    private final HashMap<BedwarsGame, Integer> createdAmountsMap;

    public WorldsManager(ConfigManager configManager, final JavaPlugin plugin) {
        this.configManager = configManager;
        this.plugin = plugin;
        this.worldContainer = plugin.getServer().getWorldContainer();
        this.pluginFolder = plugin.getDataFolder();
        this.activeGameSet = new HashSet<>();
        this.createdAmountsMap = new HashMap<>();
    }

    public final void load(BedwarsGame game, boolean forceDelete) {
        final String fileName = game.getMapFilename();
        final Integer i = createdAmountsMap.get(game);
        final int currentInt = i == null ? 1 : i;
        final String tempName = fileName + "-" + currentInt;
        final File sourceFile = new File(pluginFolder.getAbsolutePath(), game.getMapFilename());
        if (!sourceFile.exists()) {
            plugin.getLogger().warning(String.format("Could not find a world folder named [%s].", sourceFile.getAbsolutePath()));
            return;
        }
        final File outputFile = new File(worldContainer.getAbsolutePath(), tempName);
        if (forceDelete && outputFile.exists()) {
            try {
                FileUtils.deleteDirectory(outputFile);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        final WorldLoader worldLoader = new WorldLoader(sourceFile, outputFile.getAbsolutePath());
        plugin.getLogger().info(String.format("Attempting to load bukkit World [%s].", tempName));
        worldLoader.copyToDir()
                .thenAccept(v -> {
                    final World w = WorldCreator.name(tempName)
                            .environment(World.Environment.NORMAL)
                            .createWorld();
                    if (w != null) {
                        activeGameSet.add(GameUtilities.fromGamemode(game, tempName, configManager.getDefaultConfiguration().getLobbyName(), plugin));
                        createdAmountsMap.put(game, currentInt);
                        plugin.getLogger().info(String.format("Successfully created a world name [%s].", tempName));
                    } else {
                        plugin.getLogger().warning(String.format("Something went wrong when creating world [%s].", tempName));
                    }
                });
    }

    public Set<ActiveGame> getActiveGameSet() {
        return activeGameSet;
    }

    public HashMap<BedwarsGame, Integer> getCreatedAmountsMap() {
        return createdAmountsMap;
    }
}
