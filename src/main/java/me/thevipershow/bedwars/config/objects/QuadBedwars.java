package me.thevipershow.bedwars.config.objects;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShop;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import static me.thevipershow.bedwars.AllStrings.*;

@SerializableAs("Quad")
public final class QuadBedwars extends BedwarsGame implements ConfigurationSerializable {
    private QuadBedwars(int minGames, int maxGames, int minPlayers,
                        int players, List<BedwarsTeam> teams, SpawnPosition lobbySpawn,
                        String mapFilename, Set<TeamSpawnPosition> mapSpawns, List<Spawner> spawners,
                        List<Merchant> merchants, Shop shop, UpgradeShop upgradeShop,
                        int startTimer, int deathmatchStart, int tntFuse,
                        SpawnPosition spawnProtection, List<SpawnPosition> bedSpawnPositions) {
        super(Gamemode.QUAD, minGames, maxGames, minPlayers, players, teams, lobbySpawn, mapFilename, mapSpawns, spawners, merchants, bedSpawnPositions, shop, upgradeShop, startTimer, deathmatchStart, tntFuse, spawnProtection);
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }

    public static QuadBedwars deserialize(final Map<String, Object> objectMap) {
        final String filename = (String) objectMap.get(MAP_FILENAME.get());
        final int minGames = (int) objectMap.get(MIN_GAMES.get());
        final int maxGames = (int) objectMap.get(MAX_GAMES.get());
        final int players = (int) objectMap.get(PLAYERS.get());
        final int minPlayers = (int) objectMap.get(MIN_PLAYERS.get());
        final int startTimer = (int) objectMap.get(START_TIMER.get());
        final int deathmatchStart = (int) objectMap.get(DEATHMATCH_START.get());
        final int tntFuse = (int) objectMap.get(TNT_FUSE.get());
        final List<String> teams = (List<String>) objectMap.get(TEAMS.get());
        final List<BedwarsTeam> actualTeams = teams.stream().map(BedwarsTeam::valueOf).collect(Collectors.toList());
        final Map<String, Object> mapLobbySpawn = (Map<String, Object>) objectMap.get(MAP_LOBBY_SPAWN.get());
        final SpawnPosition mapLobbySpawnPos = SpawnPosition.deserialize(mapLobbySpawn);
        final List<Map<String, Object>> mapSpawns = (List<Map<String, Object>>) objectMap.get(MAP_SPAWNS.get());
        final Set<TeamSpawnPosition> mapSpawnPos = mapSpawns.stream().map(TeamSpawnPosition::deserialize).collect(Collectors.toSet());
        final List<Map<String, Object>> spawners = (List<Map<String, Object>>) objectMap.get(SPAWNERS.get());
        final List<Spawner> spawnerList = spawners.stream().map(Spawner::deserialize).collect(Collectors.toList());
        final List<Map<String, Object>> merchantsSection = (List<Map<String, Object>>) objectMap.get(MERCHANTS.get());
        final List<Merchant> merchantsList = merchantsSection.stream().map(Merchant::deserialize).collect(Collectors.toList());
        final Map<String, Object> shopSection = (Map<String, Object>) objectMap.get(SHOP.get());
        final Shop shop = Shop.deserialize(shopSection);
        final UpgradeShop upgradeShop = UpgradeShop.deserialize((Map<String, Object>) objectMap.get(UPGRADES.get()));
        final SpawnPosition spawnProtection = SpawnPosition.deserialize((Map<String, Object>) objectMap.get(SPAWN_PROTECTION.get()));
        final List<SpawnPosition> bedSpawnPositions = ((List<Map<String, Object>>) objectMap.get(BEDS_POS.get())).stream().map(SpawnPosition::deserialize).collect(Collectors.toList());
        return new QuadBedwars(minGames, maxGames, minPlayers, players, actualTeams, mapLobbySpawnPos, filename, mapSpawnPos, spawnerList, merchantsList, shop, upgradeShop, startTimer, deathmatchStart, tntFuse, spawnProtection, bedSpawnPositions);
    }
}
