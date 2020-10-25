package me.thevipershow.bedwars.config.objects;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShop;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import static me.thevipershow.bedwars.AllStrings.*;

@SerializableAs("Solo")
public class SoloBedwars extends BedwarsGame implements ConfigurationSerializable {

    private SoloBedwars(Gamemode gamemode, int minGames, int maxGames, int minPlayers, int players, List<BedwarsTeam> teams, SpawnPosition lobbySpawn, String mapFilename, Set<TeamSpawnPosition> mapSpawns, List<Spawner> spawners, List<Merchant> merchants, Shop shop, UpgradeShop upgradeShop, int startTimer, int deathmatchStart, int tntFuse) {
        super(gamemode, minGames, maxGames, minPlayers, players, teams, lobbySpawn, mapFilename, mapSpawns, spawners, merchants, shop, upgradeShop, startTimer, deathmatchStart, tntFuse);
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }

    public static SoloBedwars deserialize(Map<String, Object> objectMap) {
        String filename = (String) objectMap.get(MAP_FILENAME.get());
        int minGames = (int) objectMap.get(MIN_GAMES.get());
        int maxGames = (int) objectMap.get(MAX_GAMES.get());
        int players = (int) objectMap.get(PLAYERS.get());
        int deathmatchStart = (int) objectMap.get(DEATHMATCH_START.get());
        int minPlayers = (int) objectMap.get(MIN_PLAYERS.get());
        int startTimer = (int) objectMap.get(START_TIMER.get());
        int tntFuse = (int) objectMap.get(TNT_FUSE.get());
        List<String> teams = (List<String>) objectMap.get(TEAMS.get());
        List<BedwarsTeam> actualTeams = teams.stream().map(BedwarsTeam::valueOf).collect(Collectors.toList());
        Map<String, Object> mapLobbySpawn = (Map<String, Object>) objectMap.get(MAP_LOBBY_SPAWN.get());

        SpawnPosition mapLobbySpawnPos = SpawnPosition.deserialize(mapLobbySpawn);
        List<Map<String, Object>> mapSpawns = (List<Map<String, Object>>) objectMap.get(MAP_SPAWNS.get());
        Set<TeamSpawnPosition> mapSpawnPos = mapSpawns.stream()
                .map(TeamSpawnPosition::deserialize)
                .collect(Collectors.toSet());

        List<Map<String, Object>> spawners = (List<Map<String, Object>>) objectMap.get(SPAWNERS.get());
        List<Spawner> spawnerList = spawners.stream()
                .map(Spawner::deserialize)
                .collect(Collectors.toList());
        List<Map<String, Object>> merchantsSection = (List<Map<String, Object>>) objectMap.get(MERCHANTS.get());

        List<Merchant> merchantsList = merchantsSection.stream()
                .map(Merchant::deserialize)
                .collect(Collectors.toList());
        Map<String, Object> shopSection = (Map<String, Object>) objectMap.get(SHOP.get());
        Shop shop = Shop.deserialize(shopSection);
        UpgradeShop upgradeShop = UpgradeShop.deserialize((Map<String, Object>) objectMap.get(UPGRADES.get()));
        return new SoloBedwars(Gamemode.SOLO, minGames, maxGames, minPlayers, players, actualTeams, mapLobbySpawnPos, filename, mapSpawnPos, spawnerList, merchantsList, shop, upgradeShop, startTimer, deathmatchStart, tntFuse);
    }

}
