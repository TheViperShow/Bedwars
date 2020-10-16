package me.thevipershow.bedwars.bedwars.spawner;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("SpawnerLevel")
public class SpawnerLevel implements ConfigurationSerializable {
    private final int level;
    private final int dropIncrease;
    private final int afterSeconds;
    private final int decreaseSpawnDelay;

    public SpawnerLevel(int level, int dropIncrease, int afterSeconds, int decreaseSpawnDelay) {
        this.level = level;
        this.dropIncrease = dropIncrease;
        this.afterSeconds = afterSeconds;
        this.decreaseSpawnDelay = decreaseSpawnDelay;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("number", level);
        objectMap.put("increase-drop", dropIncrease);
        objectMap.put("after-seconds", afterSeconds);
        objectMap.put("decrease-delay", decreaseSpawnDelay);
        return objectMap;
    }

    public static SpawnerLevel deserialize(Map<String, Object> objectMap) {
        int level = (int) objectMap.get("number");
        int increase = (int) objectMap.get("increase-drop");
        int seconds = (int) objectMap.get("after-seconds");
        int decreaseSpawnDelay = (int) objectMap.get("decrease-delay");
        return new SpawnerLevel(level, increase, seconds, decreaseSpawnDelay);
    }

    public int getDecreaseSpawnDelay() {
        return decreaseSpawnDelay;
    }

    public int getLevel() {
        return level;
    }

    public int getDropIncrease() {
        return dropIncrease;
    }

    public int getAfterSeconds() {
        return afterSeconds;
    }
}
