package me.thevipershow.aussiebedwars.game;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.config.BedwarsGamemodeConfig;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.worlds.WorldsManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class GameManager {

    private final JavaPlugin plugin;
    private final WorldsManager worldsManager;

    private final Set<BedwarsGame> bedwarsGameSet = new HashSet<>();

    @SafeVarargs
    public GameManager(JavaPlugin plugin, WorldsManager worldsManager, BedwarsGamemodeConfig<? extends BedwarsGame>... configs) {
        this.plugin = plugin;
        this.worldsManager = worldsManager;
        for (final BedwarsGamemodeConfig<? extends BedwarsGame> config : configs)
            this.bedwarsGameSet.addAll(config.getSoloBedwarsObjects());
    }

    public void loadBaseAmount() {
        bedwarsGameSet.forEach(bedwarsGame -> {
            int count = 0;
            while (count < bedwarsGame.getMinGames()) {
                worldsManager.load(bedwarsGame, true);
                count++;
            }
        });
    }

    public Optional<ActiveGame> findOptimalGame(final Gamemode gamemode) {
        return worldsManager.getActiveGameSet().stream()
                .filter(game -> game.bedwarsGame.getGamemode() == gamemode)
                .reduce((a, b) -> a.bedwarsGame.getPlayers() - a.associatedQueue.queueSize() >= b.bedwarsGame.getPlayers() - b.associatedQueue.queueSize() ? a : b);
    }

    public void removeFromAllQueues(final Player player) {
        worldsManager.getActiveGameSet().forEach(b -> b.getAssociatedQueue().removeFromQueue(player));
    }

    public final WorldsManager getWorldsManager() {
        return worldsManager;
    }

    public final Set<BedwarsGame> getBedwarsGameSet() {
        return bedwarsGameSet;
    }

}
