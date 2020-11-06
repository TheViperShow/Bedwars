package me.thevipershow.bedwars.game.objects;

import java.util.Objects;
import lombok.Getter;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public final class CachedGameData {

    private final World lobby, game;
    private final Location cachedWaitingLocation, cachedServerSpawnLocation;

    public CachedGameData(String gameFilename, World lobby, BedwarsGame bedwarsGame) {
        this.lobby = Objects.requireNonNull(lobby, "A lobby world with that name could not be found!");
        this.game = Objects.requireNonNull(Bukkit.getWorld(gameFilename), "The world for this game could not be found!");
        this.cachedWaitingLocation = bedwarsGame.getLobbySpawn().toLocation(this.game);
        this.cachedServerSpawnLocation = lobby.getSpawnLocation();
    }
}
