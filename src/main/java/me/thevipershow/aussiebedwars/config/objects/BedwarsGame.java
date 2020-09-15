package me.thevipershow.aussiebedwars.config.objects;


import java.util.List;
import java.util.Set;
import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;

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
    protected final Shop shop;
    protected final int startTimer;

    protected BedwarsGame(
            Gamemode gamemode,
            int minGames,
            int maxGames,
            int minPlayers,
            int players,
            List<BedwarsTeam> teams,
            SpawnPosition lobbySpawn,
            String mapFilename,
            Set<TeamSpawnPosition> mapSpawns,
            List<Spawner> spawners,
            List<Merchant> merchants,
            Shop shop,
            int startTimer) {
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
        this.shop = shop;
        this.startTimer = startTimer;
    }

    public SpawnPosition spawnPosOfTeam(final BedwarsTeam team) {
        for (TeamSpawnPosition mapSpawn : this.mapSpawns)
            if (mapSpawn.getBedwarsTeam() == team)
                return mapSpawn;
        return null;
    }

    public Gamemode getGamemode() {
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

    public List<BedwarsTeam> getTeams() {
        return teams;
    }

    public SpawnPosition getLobbySpawn() {
        return lobbySpawn;
    }

    public String getMapFilename() {
        return mapFilename;
    }

    public Set<TeamSpawnPosition> getMapSpawns() {
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

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getStartTimer() {
        return startTimer;
    }
}
