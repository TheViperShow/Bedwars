package me.thevipershow.bedwars.game.data.game;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import me.thevipershow.bedwars.config.objects.SpawnPosition;
import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class CachedGameData {

    private final World lobby, game;
    private final Location cachedWaitingLocation, cachedServerSpawnLocation;
    private final Map<BedwarsTeam, SpawnPosition> cachedTeamSpawnPositions = new EnumMap<>(BedwarsTeam.class);
    private final HashSet<Block> cachedPlacedBlocks = new HashSet<>();

    public CachedGameData(World gameWorld, World lobby, BedwarsGame bedwarsGame) {
        this.lobby = Objects.requireNonNull(lobby, "A lobby world with that name could not be found!");
        this.game = Objects.requireNonNull(gameWorld, "The world for this game could not be found!");
        this.cachedWaitingLocation = bedwarsGame.getLobbySpawn().toLocation(this.game);
        this.cachedServerSpawnLocation = lobby.getSpawnLocation();
        for (TeamSpawnPosition mapSpawn : bedwarsGame.getMapSpawns()) {
            cachedTeamSpawnPositions.put(mapSpawn.getBedwarsTeam(), mapSpawn);
        }
    }

    public final World getLobby() {
        return lobby;
    }

    public final World getGame() {
        return game;
    }

    public final HashSet<Block> getCachedPlacedBlocks() {
        return cachedPlacedBlocks;
    }

    public final Map<BedwarsTeam, SpawnPosition> getCachedTeamSpawnPositions() {
        return cachedTeamSpawnPositions;
    }

    public final Location getCachedWaitingLocation() {
        return cachedWaitingLocation;
    }

    public final Location getCachedServerSpawnLocation() {
        return cachedServerSpawnLocation;
    }
}
