package me.thevipershow.aussiebedwars.config.objects;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.UpgradeShop;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class DuoBedwars extends BedwarsGame implements ConfigurationSerializable {
    protected DuoBedwars(Gamemode gamemode, int minGames, int maxGames, int minPlayers, int players, List<BedwarsTeam> teams, SpawnPosition lobbySpawn, String mapFilename, Set<TeamSpawnPosition> mapSpawns, List<Spawner> spawners, List<Merchant> merchants, Shop shop, UpgradeShop upgradeShop, int startTimer) {
        super(gamemode, minGames, maxGames, minPlayers, players, teams, lobbySpawn, mapFilename, mapSpawns, spawners, merchants, shop, upgradeShop, startTimer);
    }

    /**
     * Creates a Map representation of this class.
     * <p>
     * This class must provide a method to restore this class, as defined in
     * the {@link ConfigurationSerializable} interface javadocs.
     *
     * @return Map containing the current state of this class
     */
    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("You can't serialize this.");
    }

    public static DuoBedwars deserialize(final Map<String, Object> objectMap) {
        String filename = (String) objectMap.get("map-filename");
        int minGames = (int) objectMap.get("minimum-games");
        int maxGames = (int) objectMap.get("maximum-games");
        int players = (int) objectMap.get("players");
        int minPlayers = (int) objectMap.get("min-players");
        int startTimer = (int) objectMap.get("start-timer");
        List<String> teams = (List<String>) objectMap.get("teams");
        List<BedwarsTeam> actualTeams = teams.stream().map(BedwarsTeam::valueOf).collect(Collectors.toList());
        Map<String, Object> mapLobbySpawn = (Map<String, Object>) objectMap.get("map-lobby-spawn");

        SpawnPosition mapLobbySpawnPos = SpawnPosition.deserialize(mapLobbySpawn);
        List<Map<String, Object>> mapSpawns = (List<Map<String, Object>>) objectMap.get("map-spawns");
        Set<TeamSpawnPosition> mapSpawnPos = mapSpawns.stream()
                .map(TeamSpawnPosition::deserialize)
                .collect(Collectors.toSet());

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
        UpgradeShop upgradeShop = UpgradeShop.deserialize((Map<String, Object>) objectMap.get("upgrades"));
        return new DuoBedwars(Gamemode.DUO, minGames, maxGames, minPlayers, players, actualTeams, mapLobbySpawnPos, filename, mapSpawnPos, spawnerList, merchantsList, shop, upgradeShop, startTimer);
    }
}
