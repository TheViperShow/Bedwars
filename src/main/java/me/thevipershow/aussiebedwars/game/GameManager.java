package me.thevipershow.aussiebedwars.game;

import java.util.HashSet;
import java.util.Set;
import me.thevipershow.aussiebedwars.config.BedwarsGamemodeConfig;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.worlds.WorldsManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class GameManager {

    private final JavaPlugin plugin;
    private final QueueLoader queueLoader;
    private final WorldsManager worldsManager;

    private final Set<BedwarsGame> bedwarsGameSet = new HashSet<>();
    private final Set<ActiveGame> activeGames = new HashSet<>();

    @SafeVarargs
    public GameManager(JavaPlugin plugin, BedwarsGamemodeConfig<? extends BedwarsGame>... configs) {
        this.plugin = plugin;
        this.worldsManager = WorldsManager.getInstance(plugin, configs);
        for (final BedwarsGamemodeConfig<? extends BedwarsGame> config : configs)
            this.bedwarsGameSet.addAll(config.getSoloBedwarsObjects());

        this.queueLoader = new QueueLoader(this.bedwarsGameSet);
    }

    public final QueueLoader getQueueLoader() {
        return queueLoader;
    }

    public final WorldsManager getWorldsManager() {
        return worldsManager;
    }

    public final Set<BedwarsGame> getBedwarsGameSet() {
        return bedwarsGameSet;
    }

    public final Set<ActiveGame> getActiveGames() {
        return activeGames;
    }
}
