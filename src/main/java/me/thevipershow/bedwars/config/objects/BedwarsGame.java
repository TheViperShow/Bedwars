package me.thevipershow.bedwars.config.objects;

import java.util.List;
import java.util.Set;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShop;

public abstract class BedwarsGame {

    protected final Gamemode gamemode;
    protected final int minGames;
    protected final int maxGames;
    protected final int minPlayers;
    protected final int players;
    protected final List<BedwarsTeam> teams;
    protected final SpawnPosition lobbySpawn;
    protected final String mapFilename;
    protected final Set<TeamSpawnPosition> mapSpawns;
    protected final List<Spawner> spawners;
    protected final List<Merchant> merchants;
    protected final List<SpawnPosition> bedSpawnPositions;
    protected final Shop shop;
    protected final UpgradeShop upgradeShop;
    protected final int startTimer;
    protected final int deathmatchStart;
    protected final int tntFuse;
    protected final SpawnPosition spawnProtection;

    protected BedwarsGame(
            final Gamemode gamemode,
            final int minGames,
            final int maxGames,
            final int minPlayers,
            final int players,
            final List<BedwarsTeam> teams,
            final SpawnPosition lobbySpawn,
            final String mapFilename,
            final Set<TeamSpawnPosition> mapSpawns,
            final List<Spawner> spawners,
            final List<Merchant> merchants,
            final List<SpawnPosition> bedSpawnPositions,
            final Shop shop,
            final UpgradeShop upgradeShop,
            final int startTimer,
            final int deathmatchStart,
            final int tntFuse,
            final SpawnPosition spawnProtection) {
        this.gamemode = gamemode;
        this.minGames = minGames;
        this.maxGames = maxGames;
        this.minPlayers = minPlayers;
        this.players = players;
        this.teams = teams;
        this.lobbySpawn = lobbySpawn;
        this.mapFilename = mapFilename;
        this.mapSpawns = mapSpawns;
        this.spawners = spawners;
        this.merchants = merchants;
        this.bedSpawnPositions = bedSpawnPositions;
        this.shop = shop;
        this.upgradeShop = upgradeShop;
        this.startTimer = startTimer;
        this.deathmatchStart = deathmatchStart;
        this.tntFuse = tntFuse;
        this.spawnProtection = spawnProtection;
    }

    public SpawnPosition spawnPosOfTeam(final BedwarsTeam team) {
        for (TeamSpawnPosition mapSpawn : this.mapSpawns) {
            if (mapSpawn.getBedwarsTeam() == team) {
                return mapSpawn;
            }
        }
        return null;
    }

    public final Gamemode getGamemode() {
        return gamemode;
    }

    public final int getMinGames() {
        return minGames;
    }

    public final int getMaxGames() {
        return maxGames;
    }

    public final int getPlayers() {
        return players;
    }

    public final List<BedwarsTeam> getTeams() {
        return teams;
    }

    public final int getDeathmatchStart() {
        return deathmatchStart;
    }

    public final SpawnPosition getLobbySpawn() {
        return lobbySpawn;
    }

    public final String getMapFilename() {
        return mapFilename;
    }

    public final Set<TeamSpawnPosition> getMapSpawns() {
        return mapSpawns;
    }

    public final List<Spawner> getSpawners() {
        return spawners;
    }

    public final List<Merchant> getMerchants() {
        return merchants;
    }

    public final Shop getShop() {
        return shop;
    }

    public final int getMinPlayers() {
        return minPlayers;
    }

    public final int getStartTimer() {
        return startTimer;
    }

    public final List<SpawnPosition> getBedSpawnPositions() {
        return bedSpawnPositions;
    }

    public final UpgradeShop getUpgradeShop() {
        return upgradeShop;
    }

    public final int getTntFuse() {
        return tntFuse;
    }

    public final SpawnPosition getSpawnProtection() {
        return spawnProtection;
    }
}
