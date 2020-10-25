package me.thevipershow.bedwars.config.objects;

import java.util.Map;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
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
    protected TeamSpawnPosition clone() throws CloneNotSupportedException {
        return (TeamSpawnPosition) super.clone();
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }

    public static TeamSpawnPosition deserialize(final Map<String, Object> map) {
        final SpawnPosition spawnPosition = SpawnPosition.deserialize(map);
        final BedwarsTeam team = BedwarsTeam.valueOf(((String) map.get(AllStrings.TEAM.get())).toUpperCase());
        return new TeamSpawnPosition(spawnPosition, team);
    }

    public BedwarsTeam getBedwarsTeam() {
        return bedwarsTeam;
    }
}
