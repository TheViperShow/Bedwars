package me.thevipershow.aussiebedwars.bedwars.objects;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("SpawnPosition")
public class SpawnPosition implements ConfigurationSerializable {

    private final double x;
    private final double y;
    private final double z;

    public SpawnPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> positionMap = new HashMap<>();
        positionMap.put("x", x);
        positionMap.put("y", y);
        positionMap.put("z", z);
        return positionMap;
    }

    public static SpawnPosition deserialize(final Map<String, Object> objectMap) {
        double x, y, z;
        x = (double) objectMap.get("x");
        y = (double) objectMap.get("y");
        z = (double) objectMap.get("z");
        return new SpawnPosition(x, y, z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
