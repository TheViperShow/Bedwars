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
public class SoloBedwars extends BedwarsGame implements ConfigurationSerializable {

    protected SoloBedwars(
            Gamemode gamemode,
            int minGames,
            int maxGames,
            int minPlayers,
            int players,
            Set<BedwarsTeam> teams,
            SpawnPosition lobbySpawn,
            String mapFilename,
            List<SpawnPosition> mapSpawns,
            List<Spawner> spawners,
            List<Merchant> merchants, Shop shop) {
        super(gamemode, minGames, maxGames, minPlayers, players, teams, lobbySpawn, mapFilename, mapSpawns, spawners, merchants, shop);
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
                .map(SpawnPosition::deserialize)
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


        return new SoloBedwars(Gamemode.SOLO, minGames, maxGames, minGames, players, actualTeams, mapLobbySpawnPos, filename, mapSpawnPos, spawnerList, merchantsList, shop);
    }

}
