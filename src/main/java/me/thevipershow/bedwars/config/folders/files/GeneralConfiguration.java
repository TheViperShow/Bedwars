package me.thevipershow.bedwars.config.folders.files;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.config.folders.ConfigFiles;
import me.thevipershow.bedwars.config.objects.SpawnPosition;

public final class GeneralConfiguration extends AbstractFileConfig {

    private final Gamemode gamemode;
    private final String mapFilename;
    private final int startTimer;
    private final int minGames;
    private final int maxGames;
    private final int minPlayers;
    private final int players;
    private final int deathmatchStart;
    private final int tntFuse;
    private final SpawnPosition mapProtection;
    private final SpawnPosition lobbySpawn;

    public GeneralConfiguration(File file) {
        super(file, ConfigFiles.GENERAL_FILE);
        gamemode = Gamemode.valueOf(getConfiguration().getString(AllStrings.GAMEMODE.get()).toUpperCase(Locale.ROOT));
        mapFilename = getConfiguration().getString(AllStrings.MAP_FILENAME.get());
        startTimer = getConfiguration().getInt(AllStrings.START_TIMER.get());
        minGames = getConfiguration().getInt(AllStrings.MIN_GAMES.get());
        maxGames = getConfiguration().getInt(AllStrings.MAX_GAMES.get());
        minPlayers = getConfiguration().getInt(AllStrings.MIN_PLAYERS.get());
        players = getConfiguration().getInt(AllStrings.PLAYERS.get());
        deathmatchStart = getConfiguration().getInt(AllStrings.DEATHMATCH_START.get());
        tntFuse = getConfiguration().getInt(AllStrings.TNT_FUSE.get());
        mapProtection = SpawnPosition.deserialize(getMap(AllStrings.SPAWN_PROTECTION.get()));
        lobbySpawn = SpawnPosition.deserialize(getMap(AllStrings.MAP_LOBBY_SPAWN.get()));
    }

    public Gamemode getGamemode() {
        return gamemode;
    }

    public String getMapFilename() {
        return mapFilename;
    }

    public int getStartTimer() {
        return startTimer;
    }

    public int getMinGames() {
        return minGames;
    }

    public int getMaxGames() {
        return maxGames;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getPlayers() {
        return players;
    }

    public int getDeathmatchStart() {
        return deathmatchStart;
    }

    public int getTntFuse() {
        return tntFuse;
    }

    public SpawnPosition getMapProtection() {
        return mapProtection;
    }

    public SpawnPosition getLobbySpawn() {
        return lobbySpawn;
    }
}
