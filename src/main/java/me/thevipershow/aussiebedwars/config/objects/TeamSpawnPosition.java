package me.thevipershow.aussiebedwars.config.objects;

import java.util.Map;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("TeamSpawnPosition")
public class TeamSpawnPosition extends SpawnPosition implements ConfigurationSerializable {
    private final BedwarsTeam bedwarsTeam;

    public TeamSpawnPosition(SpawnPosition spawnPosition, BedwarsTeam bedwarsTeam) {
        super(spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ());
        this.bedwarsTeam = bedwarsTeam;
    }

    public TeamSpawnPosition(double x, double y, double z, BedwarsTeam bedwarsTeam) {
        super(x, y, z);
        this.bedwarsTeam = bedwarsTeam;
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("I don't want to write this.");
    }

    public static TeamSpawnPosition deserialize(final Map<String, Object> map) {
        final SpawnPosition spawnPosition = SpawnPosition.deserialize(map);
        final BedwarsTeam team = BedwarsTeam.valueOf(((String) map.get("team")).toUpperCase());
        return new TeamSpawnPosition(spawnPosition, team);
    }

    public BedwarsTeam getBedwarsTeam() {
        return bedwarsTeam;
    }
}
