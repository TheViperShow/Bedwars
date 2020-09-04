package me.thevipershow.aussiebedwars.worlds;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import me.thevipershow.aussiebedwars.config.SoloConfig;
import me.thevipershow.aussiebedwars.config.objects.SoloBedwars;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldsManager {

    private static WorldsManager instance = null;
    private final SoloConfig soloConfig;
    private final JavaPlugin plugin;
    private final File worldContainer;
    private final File pluginFolder;

    private WorldsManager(final JavaPlugin plugin, final SoloConfig soloConfig) {
        this.plugin = plugin;
        this.soloConfig = soloConfig;
        this.worldContainer = plugin.getServer().getWorldContainer();
        this.pluginFolder = plugin.getDataFolder();
    }

    public synchronized static WorldsManager getInstance(JavaPlugin plugin, SoloConfig soloConfig) {
        if (instance == null)
            instance = new WorldsManager(plugin, soloConfig);
        return instance;
    }

    private final HashMap<SoloBedwars, World> worldHashMap = new HashMap<>();

    public final void loadBaseAmount() {
        final List<SoloBedwars> games = soloConfig.getSoloBedwarsConfigurations();
        for (final SoloBedwars game : games) {
            final int minimumGames = game.getMinGames();
            final String worldName = game.getMapFilename();
            if (minimumGames < 1) continue;

            File sourceFile = new File(pluginFolder.getAbsolutePath(), worldName);
            if (!sourceFile.exists()) {
                plugin.getLogger().warning(String.format("Could not find a world folder named [%s].", sourceFile.getAbsolutePath()));
                return;
            }

            int created = 0;
            while (created < minimumGames) {
                created++;
                final String tempName = worldName + "-" + created;
                final WorldLoader worldLoader = new WorldLoader(sourceFile, tempName);
                worldLoader.copyToDir().thenAccept(v -> {
                    plugin.getLogger().info(String.format("Attempting to load bukkit World [%s].", tempName));
                    final World w = WorldCreator.name(tempName)
                            .environment(World.Environment.NORMAL)
                            .createWorld();
                    if (w != null) {
                        worldHashMap.put(game, w);
                        plugin.getLogger().info(String.format("Successfully created a world name [%s].", tempName));
                    } else {
                        plugin.getLogger().warning(String.format("Something went wrong when creating world [%s].", tempName));
                    }
                });
            }
        }
    }

    public HashMap<SoloBedwars, World> getWorldHashMap() {
        return worldHashMap;
    }
}
