package me.thevipershow.bedwars.config.objects;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static me.thevipershow.bedwars.AllStrings.BEDS_POS;
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
import static me.thevipershow.bedwars.AllStrings.SPAWN_PROTECTION;
import static me.thevipershow.bedwars.AllStrings.START_TIMER;
import static me.thevipershow.bedwars.AllStrings.TEAMS;
import static me.thevipershow.bedwars.AllStrings.TNT_FUSE;
import static me.thevipershow.bedwars.AllStrings.UPGRADES;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShop;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class DuoBedwars extends BedwarsGame implements ConfigurationSerializable {
    protected DuoBedwars(final int minGames, final int maxGames, final int minPlayers,
                         final int players, final List<BedwarsTeam> teams, final SpawnPosition lobbySpawn, final String mapFilename,
                         final Set<TeamSpawnPosition> mapSpawns, final List<Spawner> spawners, final List<Merchant> merchants,
                         final Shop shop, final UpgradeShop upgradeShop, final int startTimer, final int deathmatchStart,
                         final int tntFuse, final SpawnPosition spawnProtection, List<SpawnPosition> bedSpawnPositions) {

        super(Gamemode.DUO, minGames, maxGames,
                minPlayers, players, teams,
                lobbySpawn, mapFilename, mapSpawns,
                spawners, merchants, bedSpawnPositions, shop,
                upgradeShop, startTimer, deathmatchStart,
                tntFuse, spawnProtection);
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
        final Set<TeamSpawnPosition> mapSpawnPos = mapSpawns.stream()
                .map(TeamSpawnPosition::deserialize)
                .collect(Collectors.toSet());

        final List<Map<String, Object>> spawners = (List<Map<String, Object>>) objectMap.get(SPAWNERS.get());
        final List<Spawner> spawnerList = spawners.stream()
                .map(Spawner::deserialize)
                .collect(Collectors.toList());
        final List<Map<String, Object>> merchantsSection = (List<Map<String, Object>>) objectMap.get(MERCHANTS.get());

        final List<Merchant> merchantsList = merchantsSection.stream()
                .map(Merchant::deserialize)
                .collect(Collectors.toList());
        final Map<String, Object> shopSection = (Map<String, Object>) objectMap.get(SHOP.get());
        final Shop shop = Shop.deserialize(shopSection);
        final UpgradeShop upgradeShop = UpgradeShop.deserialize((Map<String, Object>) objectMap.get(UPGRADES.get()));
        final SpawnPosition spawnProtection = SpawnPosition.deserialize((Map<String, Object>) objectMap.get(SPAWN_PROTECTION.get()));
        final List<SpawnPosition> bedSpawnPositions = ((List<Map<String, Object>>) objectMap.get(BEDS_POS.get())).stream().map(SpawnPosition::deserialize).collect(Collectors.toList());
        return new DuoBedwars(minGames, maxGames, minPlayers, players, actualTeams, mapLobbySpawnPos, filename, mapSpawnPos, spawnerList, merchantsList, shop, upgradeShop, startTimer, deathmatchStart, tntFuse, spawnProtection, bedSpawnPositions);
    }
}
