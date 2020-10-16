package me.thevipershow.bedwars.config.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.spawner.SpawnerLevel;
import me.thevipershow.bedwars.bedwars.objects.spawners.SpawnerType;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Spawner")
public class Spawner implements ConfigurationSerializable {
    private final SpawnPosition spawnPosition;
    private final SpawnerType spawnerType;
    private final int dropDelay;
    private final int dropAmount;
    private final boolean invisible;
    private final List<SpawnerLevel> spawnerLevels;

    public Spawner(SpawnPosition spawnPosition, SpawnerType spawnerType, int dropDelay, int dropAmount, boolean invisible, List<SpawnerLevel> spawnerLevels) {
        this.spawnPosition = spawnPosition;
        this.spawnerType = spawnerType;
        this.dropDelay = dropDelay;
        this.dropAmount = dropAmount;
        this.invisible = invisible;
        this.spawnerLevels = spawnerLevels;
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("Cannot serialize spawner.");
    }

    public static Spawner deserialize(Map<String, Object> map) {
        SpawnerType spawnerType = SpawnerType.valueOf((String) map.get("type"));
        SpawnPosition sp = SpawnPosition.deserialize((Map<String, Object>) map.get("location"));
        int drop = (int) map.get("drop-amount");
        List<SpawnerLevel> levels = new ArrayList<>();
        final List<Map<String, Object>> objects = (List<Map<String, Object>>) map.get("levels");
        objects.forEach(o -> levels.add(SpawnerLevel.deserialize(o)));
        final int dropDelay = (int) map.get("drop-delay");
        final boolean inv = (boolean) map.get("invisible");
        return new Spawner(sp, spawnerType, dropDelay, drop, inv, levels);
    }

    public SpawnPosition getSpawnPosition() {
        return spawnPosition;
    }

    public SpawnerType getSpawnerType() {
        return spawnerType;
    }

    public int getDropAmount() {
        return dropAmount;
    }

    public List<SpawnerLevel> getSpawnerLevels() {
        return spawnerLevels;
    }

    public int getDropDelay() {
        return dropDelay;
    }

    public boolean isInvisible() {
        return invisible;
    }

    @Override
    public String toString() {
        return "Spawner{" +
                "spawnPosition=" + spawnPosition +
                ", spawnerType=" + spawnerType +
                ", dropDelay=" + dropDelay +
                ", dropAmount=" + dropAmount +
                ", spawnerLevels=" + spawnerLevels +
                '}';
    }
}
