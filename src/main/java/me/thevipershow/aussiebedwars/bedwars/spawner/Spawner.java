package me.thevipershow.aussiebedwars.bedwars.spawner;

import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.thevipershow.aussiebedwars.bedwars.objects.SpawnPosition;
import me.thevipershow.aussiebedwars.bedwars.objects.spawners.SpawnerType;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Spawner")
public class Spawner implements ConfigurationSerializable {
    private final SpawnPosition spawnPosition;
    private final SpawnerType spawnerType;
    private int dropAmount;
    private List<SpawnerLevel> spawnerLevels;

    public Spawner(SpawnPosition spawnPosition, SpawnerType spawnerType, int dropAmount, List<SpawnerLevel> spawnerLevels) {
        this.spawnPosition = spawnPosition;
        this.spawnerType = spawnerType;
        this.dropAmount = dropAmount;
        this.spawnerLevels = spawnerLevels;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }

    public static Spawner deserialize(Map<String, Object> map) {
        SpawnerType spawnerType = SpawnerType.valueOf((String) map.get("type"));
        SpawnPosition sp = new SpawnPosition((double) map.get("location.x"),
                (double) map.get("location.y"),
                (double) map.get("location.z"));
        int drop = (int) map.get("drop-amount");
        List<SpawnerLevel> levels = new ArrayList<>();
        final List<Map<String, Object>> objects = (List<Map<String, Object>>) map.get("levels");
        objects.forEach(o -> levels.add(SpawnerLevel.deserialize(o)));
        return new Spawner(sp, spawnerType, drop, levels);
    }
}
