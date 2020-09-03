package me.thevipershow.aussiebedwars.bedwars.spawner;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("SpawnerLevel")
public class SpawnerLevel implements ConfigurationSerializable {
    private final int level;
    private final int dropIncrease;
    private final long afterSeconds;

    public SpawnerLevel(int level, int dropIncrease, long afterSeconds) {
        this.level = level;
        this.dropIncrease = dropIncrease;
        this.afterSeconds = afterSeconds;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("number", level);
        objectMap.put("increase-drop", dropIncrease);
        objectMap.put("after-seconds", afterSeconds);
        return objectMap;
    }

    public static SpawnerLevel deserialize(Map<String, Object> objectMap) {
        int level = (int) objectMap.get("number");
        int increase = (int) objectMap.get("increase-drop");
        long seconds = (long) objectMap.get("after-seconds");
        return new SpawnerLevel(level, increase, seconds);
    }
}
