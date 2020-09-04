package me.thevipershow.aussiebedwars.config.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.thevipershow.aussiebedwars.bedwars.spawner.SpawnerLevel;
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
        SpawnPosition sp = SpawnPosition.deserialize((Map<String, Object>) map.get("location"));
        int drop = (int) map.get("drop-amount");
        List<SpawnerLevel> levels = new ArrayList<>();
        final List<Map<String, Object>> objects = (List<Map<String, Object>>) map.get("levels");
        objects.forEach(o -> levels.add(SpawnerLevel.deserialize(o)));
        return new Spawner(sp, spawnerType, drop, levels);
    }
}
