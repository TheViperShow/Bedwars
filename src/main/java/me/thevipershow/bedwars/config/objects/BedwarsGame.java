package me.thevipershow.bedwars.config.objects;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.folders.ConfigFiles;
import me.thevipershow.bedwars.config.folders.files.BedsConfiguration;
import me.thevipershow.bedwars.config.folders.files.GeneralConfiguration;
import me.thevipershow.bedwars.config.folders.files.MerchantsConfiguration;
import me.thevipershow.bedwars.config.folders.files.ShopConfiguration;
import me.thevipershow.bedwars.config.folders.files.SpawnersConfiguration;
import me.thevipershow.bedwars.config.folders.files.SpawnsConfiguration;
import me.thevipershow.bedwars.config.folders.files.TeamsConfiguration;
import me.thevipershow.bedwars.config.folders.files.UpgradesConfiguration;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShop;

public final class BedwarsGame {

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
    protected final List<TeamSpawnPosition> bedSpawnPositions;
    protected final Shop shop;
    protected final UpgradeShop upgradeShop;
    protected final int startTimer;
    protected final int deathmatchStart;
    protected final int tntFuse;
    protected final SpawnPosition spawnProtection;
    protected final File configurationFolder;

    public BedwarsGame(Map<ConfigFiles, File> mappings, File configurationFolder) {
        UpgradesConfiguration upgradesConfiguration = new UpgradesConfiguration(mappings.get(ConfigFiles.UPGRADES_FILE));
        TeamsConfiguration teamsConfiguration = new TeamsConfiguration(mappings.get(ConfigFiles.TEAMS_FILE));
        SpawnsConfiguration spawnsConfiguration = new SpawnsConfiguration(mappings.get(ConfigFiles.SPAWNS_FILE));
        SpawnersConfiguration spawnersConfiguration = new SpawnersConfiguration(mappings.get(ConfigFiles.SPAWNERS_FILE));
        ShopConfiguration shopConfiguration = new ShopConfiguration(mappings.get(ConfigFiles.SHOP_FILE));
        MerchantsConfiguration merchantsConfiguration = new MerchantsConfiguration(mappings.get(ConfigFiles.MERCHANTS_FILE));
        GeneralConfiguration generalConfiguration = new GeneralConfiguration(mappings.get(ConfigFiles.GENERAL_FILE));
        BedsConfiguration bedsConfiguration = new BedsConfiguration(mappings.get(ConfigFiles.BEDS_FILE));
        this.gamemode = generalConfiguration.getGamemode();
        this.minGames = generalConfiguration.getMinGames();
        this.maxGames = generalConfiguration.getMaxGames();
        this.minPlayers = generalConfiguration.getMinPlayers();
        this.players = generalConfiguration.getPlayers();
        this.lobbySpawn = generalConfiguration.getLobbySpawn();
        this.mapFilename = generalConfiguration.getMapFilename();
        this.teams = teamsConfiguration.getActualTeams();
        this.mapSpawns = spawnsConfiguration.getMapSpawnPos();
        this.spawners = spawnersConfiguration.getSpawnerList();
        this.merchants = merchantsConfiguration.getMerchantsList();
        this.bedSpawnPositions = bedsConfiguration.getBedSpawnPositions();
        this.shop = shopConfiguration.getShop();
        this.upgradeShop = upgradesConfiguration.getUpgradeShop();
        this.startTimer = generalConfiguration.getStartTimer();
        this.deathmatchStart = generalConfiguration.getDeathmatchStart();
        this.tntFuse = generalConfiguration.getTntFuse();
        this.spawnProtection = generalConfiguration.getMapProtection();
        this.configurationFolder = configurationFolder;
    }

    public BedwarsGame(UpgradesConfiguration upgradesConfiguration, TeamsConfiguration teamsConfiguration, SpawnsConfiguration spawnsConfiguration,
                          SpawnersConfiguration spawnersConfiguration, ShopConfiguration shopConfiguration, MerchantsConfiguration merchantsConfiguration,
                          GeneralConfiguration generalConfiguration, BedsConfiguration bedsConfiguration, File configurationFolder) {
        this.gamemode = generalConfiguration.getGamemode();
        this.minGames = generalConfiguration.getMinGames();
        this.maxGames = generalConfiguration.getMaxGames();
        this.minPlayers = generalConfiguration.getMinPlayers();
        this.players = generalConfiguration.getPlayers();
        this.lobbySpawn = generalConfiguration.getLobbySpawn();
        this.mapFilename = generalConfiguration.getMapFilename();
        this.teams = teamsConfiguration.getActualTeams();
        this.mapSpawns = spawnsConfiguration.getMapSpawnPos();
        this.spawners = spawnersConfiguration.getSpawnerList();
        this.merchants = merchantsConfiguration.getMerchantsList();
        this.bedSpawnPositions = bedsConfiguration.getBedSpawnPositions();
        this.shop = shopConfiguration.getShop();
        this.upgradeShop = upgradesConfiguration.getUpgradeShop();
        this.startTimer = generalConfiguration.getStartTimer();
        this.deathmatchStart = generalConfiguration.getDeathmatchStart();
        this.tntFuse = generalConfiguration.getTntFuse();
        this.spawnProtection = generalConfiguration.getMapProtection();
        this.configurationFolder = configurationFolder;
    }

    public BedwarsGame(
            final Gamemode gamemode, final int minGames, final int maxGames,
            final int minPlayers, final int players, final List<BedwarsTeam> teams,
            final SpawnPosition lobbySpawn, final String mapFilename, final Set<TeamSpawnPosition> mapSpawns,
            final List<Spawner> spawners, final List<Merchant> merchants, final List<TeamSpawnPosition> bedSpawnPositions,
            final Shop shop, final UpgradeShop upgradeShop, final int startTimer,
            final int deathmatchStart, final int tntFuse, final SpawnPosition spawnProtection, File configurationFolder) {
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
        this.configurationFolder = configurationFolder;
    }

    public final SpawnPosition spawnPosOfTeam(final BedwarsTeam team) {
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

    public final List<TeamSpawnPosition> getBedSpawnPositions() {
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

    public final File getConfigurationFolder() {
        return configurationFolder;
    }
}
