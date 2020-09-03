package me.thevipershow.aussiebedwars.bedwars.objects.games;

import java.util.List;
import java.util.Map;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.bedwars.objects.SpawnPosition;
import me.thevipershow.aussiebedwars.bedwars.objects.shops.Merchant;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("SoloGame")
public final class SoloGame implements ConfigurationSerializable {

    private final String fileName;
    private final int minGames;
    private final int maxGames;
    private final BedwarsTeam availableTeams;
    private final SpawnPosition mapLobbySpawn;
    private final List<SpawnPosition> teamSpawns;
    private final List<Merchant> availableMerchants;

    public SoloGame(String fileName,
                    int minGames,
                    int maxGames,
                    BedwarsTeam availableTeams,
                    SpawnPosition mapLobbySpawn,
                    List<SpawnPosition> teamSpawns,
                    List<Merchant> availableMerchants) {
        this.fileName = fileName;
        this.minGames = minGames;
        this.maxGames = maxGames;
        this.availableTeams = availableTeams;
        this.mapLobbySpawn = mapLobbySpawn;
        this.teamSpawns = teamSpawns;
        this.availableMerchants = availableMerchants;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }

    public static SoloGame deserialize(final Map<String, Object> objectMap) {
        //TODO: finish this
    }

    public String getFileName() {
        return fileName;
    }

    public int getMinGames() {
        return minGames;
    }

    public int getMaxGames() {
        return maxGames;
    }

    public BedwarsTeam getAvailableTeams() {
        return availableTeams;
    }

    public SpawnPosition getMapLobbySpawn() {
        return mapLobbySpawn;
    }

    public List<SpawnPosition> getTeamSpawns() {
        return teamSpawns;
    }

    public List<Merchant> getAvailableMerchants() {
        return availableMerchants;
    }
}
