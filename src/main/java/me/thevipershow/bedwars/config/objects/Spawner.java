package me.thevipershow.bedwars.config.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.AllStrings;
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
        throw new UnsupportedOperationException();
    }

    public static Spawner deserialize(Map<String, Object> map) {
        SpawnerType spawnerType = SpawnerType.valueOf((String) map.get(AllStrings.TYPE.get()));
        SpawnPosition sp = SpawnPosition.deserialize((Map<String, Object>) map.get(AllStrings.LOCATION.get()));
        int drop = (int) map.get(AllStrings.DROP_AMOUNT.get());
        List<SpawnerLevel> levels = new ArrayList<>();
        final List<Map<String, Object>> objects = (List<Map<String, Object>>) map.get(AllStrings.LEVELS.get());
        objects.forEach(o -> levels.add(SpawnerLevel.deserialize(o)));
        final int dropDelay = (int) map.get(AllStrings.DROP_DELAY.get());
        final boolean inv = (boolean) map.get(AllStrings.INVISIBLE.get());
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
}
