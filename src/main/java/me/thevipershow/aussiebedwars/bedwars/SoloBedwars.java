package me.thevipershow.aussiebedwars.bedwars;

import java.util.List;
import java.util.Map;
import java.util.Set;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.bedwars.objects.SpawnPosition;
import me.thevipershow.aussiebedwars.bedwars.objects.shops.Merchant;
import me.thevipershow.aussiebedwars.bedwars.spawner.Spawner;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Solo")
public class SoloBedwars implements ConfigurationSerializable {
    private static final Gamemode gamemode = Gamemode.SOLO;

    private final int minGames;
    private final int maxGames;
    private final int players;
    private final Set<BedwarsTeam> teams;
    private final SpawnPosition lobbySpawn;
    private final String mapFilename;
    private final List<SpawnPosition> mapSpawns;
    private final List<Spawner> spawners;
    private final List<Merchant> merchants;

    public SoloBedwars(int minGames,
                       int maxGames,
                       int players,
                       Set<BedwarsTeam> teams,
                       SpawnPosition lobbySpawn,
                       String mapFilename,
                       List<SpawnPosition> mapSpawns,
                       List<Spawner> spawners,
                       List<Merchant> merchants) {
        this.minGames = minGames;
        this.maxGames = maxGames;
        this.players = players;
        this.teams = teams;
        this.lobbySpawn = lobbySpawn;
        this.mapFilename = mapFilename;
        this.mapSpawns = mapSpawns;
        this.spawners = spawners;
        this.merchants = merchants;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }

    public static SoloBedwars deserialize(Map<String, Object> objectMap) {
        return null; //TODO: implement
    }
}
