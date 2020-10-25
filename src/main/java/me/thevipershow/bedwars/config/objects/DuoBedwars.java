package me.thevipershow.bedwars.config.objects;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static me.thevipershow.bedwars.AllStrings.DEATHMATCH_START;
import static me.thevipershow.bedwars.AllStrings.MAP_FILENAME;
import static me.thevipershow.bedwars.AllStrings.MAP_LOBBY_SPAWN;
import static me.thevipershow.bedwars.AllStrings.MAP_SPAWNS;
import static me.thevipershow.bedwars.AllStrings.MAX_GAMES;
import static me.thevipershow.bedwars.AllStrings.MERCHANTS;
import static me.thevipershow.bedwars.AllStrings.MIN_GAMES;
import static me.thevipershow.bedwars.AllStrings.MIN_PLAYERS;
import static me.thevipershow.bedwars.AllStrings.PLAYERS;
import static me.thevipershow.bedwars.AllStrings.SHOP;
import static me.thevipershow.bedwars.AllStrings.SPAWNERS;
import static me.thevipershow.bedwars.AllStrings.START_TIMER;
import static me.thevipershow.bedwars.AllStrings.TEAMS;
import static me.thevipershow.bedwars.AllStrings.TNT_FUSE;
import static me.thevipershow.bedwars.AllStrings.UPGRADES;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShop;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class DuoBedwars extends BedwarsGame implements ConfigurationSerializable {
    protected DuoBedwars(int minGames, int maxGames, int minPlayers, int players, List<BedwarsTeam> teams, SpawnPosition lobbySpawn, String mapFilename, Set<TeamSpawnPosition> mapSpawns, List<Spawner> spawners, List<Merchant> merchants, Shop shop, UpgradeShop upgradeShop, int startTimer, int deathmatchStart, int tntFuse) {
        super(Gamemode.DUO, minGames, maxGames, minPlayers, players, teams, lobbySpawn, mapFilename, mapSpawns, spawners, merchants, shop, upgradeShop, startTimer, deathmatchStart, tntFuse);
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
        throw new UnsupportedOperationException();
    }

    public static DuoBedwars deserialize(final Map<String, Object> objectMap) {
        String filename = (String) objectMap.get(MAP_FILENAME.get());
        int minGames = (int) objectMap.get(MIN_GAMES.get());
        int maxGames = (int) objectMap.get(MAX_GAMES.get());
        int players = (int) objectMap.get(PLAYERS.get());
        int minPlayers = (int) objectMap.get(MIN_PLAYERS.get());
        int startTimer = (int) objectMap.get(START_TIMER.get());
        int deathmatchStart = (int) objectMap.get(DEATHMATCH_START.get());
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
        return new DuoBedwars(minGames, maxGames, minPlayers, players, actualTeams, mapLobbySpawnPos, filename, mapSpawnPos, spawnerList, merchantsList, shop, upgradeShop, startTimer, deathmatchStart, tntFuse);
    }
}
