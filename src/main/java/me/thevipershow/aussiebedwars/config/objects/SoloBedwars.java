package me.thevipershow.aussiebedwars.config.objects;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
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
    private final Shop shop;

    public SoloBedwars(int minGames,
                       int maxGames,
                       int players,
                       Set<BedwarsTeam> teams,
                       SpawnPosition lobbySpawn,
                       String mapFilename,
                       List<SpawnPosition> mapSpawns,
                       List<Spawner> spawners,
                       List<Merchant> merchants,
                       Shop shop) {
        this.minGames = minGames;
        this.maxGames = maxGames;
        this.players = players;
        this.teams = teams;
        this.lobbySpawn = lobbySpawn;
        this.mapFilename = mapFilename;
        this.mapSpawns = mapSpawns;
        this.spawners = spawners;
        this.merchants = merchants;
        this.shop = shop;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }

    public static SoloBedwars deserialize(Map<String, Object> objectMap) {
        String filename = (String) objectMap.get("map-filename");
        int minGames = (int) objectMap.get("minimum-games");
        int maxGames = (int) objectMap.get("maximum-games");
        int players = (int) objectMap.get("players");
        List<String> teams = (List<String>) objectMap.get("teams");
        Set<BedwarsTeam> actualTeams = teams.stream().map(BedwarsTeam::valueOf).collect(Collectors.toSet());
        double spawnX, spawnY, spawnZ;
        Map<String, Object> mapLobbySpawn = (Map<String, Object>) objectMap.get("map-lobby-spawn");

        SpawnPosition mapLobbySpawnPos = SpawnPosition.deserialize(mapLobbySpawn);
        List<Map<String, Object>> mapSpawns = (List<Map<String, Object>>) objectMap.get("map-spawns");
        List<SpawnPosition> mapSpawnPos = mapSpawns.stream()
                .map(o -> SpawnPosition.deserialize(o))
                .collect(Collectors.toList());

        List<Map<String, Object>> spawners = (List<Map<String, Object>>) objectMap.get("spawners");
        List<Spawner> spawnerList = spawners.stream()
                .map(Spawner::deserialize)
                .collect(Collectors.toList());
        List<Map<String, Object>> merchantsSection = (List<Map<String, Object>>) objectMap.get("merchants");

        List<Merchant> merchantsList = merchantsSection.stream()
                .map(Merchant::deserialize)
                .collect(Collectors.toList());
        Map<String, Object> shopSection = (Map<String, Object>) objectMap.get("shop");
        Shop shop = Shop.deserialize(shopSection);


        return new SoloBedwars(minGames, maxGames, players, actualTeams, mapLobbySpawnPos, filename, mapSpawnPos, spawnerList, merchantsList, shop);
    }

    public static Gamemode getGamemode() {
        return gamemode;
    }

    public int getMinGames() {
        return minGames;
    }

    public int getMaxGames() {
        return maxGames;
    }

    public int getPlayers() {
        return players;
    }

    public Set<BedwarsTeam> getTeams() {
        return teams;
    }

    public SpawnPosition getLobbySpawn() {
        return lobbySpawn;
    }

    public String getMapFilename() {
        return mapFilename;
    }

    public List<SpawnPosition> getMapSpawns() {
        return mapSpawns;
    }

    public List<Spawner> getSpawners() {
        return spawners;
    }

    public List<Merchant> getMerchants() {
        return merchants;
    }

    public Shop getShop() {
        return shop;
    }
}
