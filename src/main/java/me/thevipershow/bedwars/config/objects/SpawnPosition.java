package me.thevipershow.bedwars.config.objects;

import java.util.HashMap;
import java.util.Map;
import me.thevipershow.bedwars.AllStrings;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("SpawnPosition")
public class SpawnPosition implements ConfigurationSerializable, Cloneable {

    private final double x;
    private final double y;
    private final double z;

    private Location cachedLoc = null;

    public SpawnPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> positionMap = new HashMap<>();
        positionMap.put(AllStrings.X.get(), x);
        positionMap.put(AllStrings.Y.get(), y);
        positionMap.put(AllStrings.Z.get(), z);
        return positionMap;
    }

    public static SpawnPosition deserialize(final Map<String, Object> objectMap) {
        double x, y, z;
        x = (double) objectMap.get(AllStrings.X.get());
        y = (double) objectMap.get(AllStrings.Y.get());
        z = (double) objectMap.get(AllStrings.Z.get());
        return new SpawnPosition(x, y, z);
    }

    @Override
    protected SpawnPosition clone() throws CloneNotSupportedException {
        return (SpawnPosition) super.clone();
    }

    public final double xDistance(final SpawnPosition position) {
        return Math.abs(x - position.x);
    }

    public final double yDistance(final SpawnPosition position) {
        return Math.abs(y - position.y);
    }

    public final double zDistance(final SpawnPosition position) {
        return Math.abs(z - position.z);
    }

    public final double xDistance(final Location location) {
        return Math.abs(x - location.getX());
    }

    public final double yDistance(final Location location) {
        return Math.abs(y - location.getY());
    }

    public final double zDistance(final Location location) {
        return Math.abs(z - location.getZ());
    }

    public final double distance(final SpawnPosition spawnPosition) {
        return Math.sqrt(squaredDistance(spawnPosition));
    }

    public final double distance(final Location location) {
        return Math.sqrt(squaredDistance(location));
    }

    public final double squaredDistance(final SpawnPosition spawnPosition) {
        return Math.pow((x - spawnPosition.x), 2.0)
                + Math.pow((y - spawnPosition.y), 2.0)
                + Math.pow((z - spawnPosition.z), 2.0);
    }

    public final double squaredDistance(final Location location) {
        return Math.pow((x - location.getX()), 2.0)
                + Math.pow((y - location.getY()), 2.0)
                + Math.pow((z - location.getZ()), 2.0);
    }

    public final SpawnPosition add(final double x, final double y, final double z) {
        return new SpawnPosition(this.x + x, this.y + y, this.z + z);
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

    public final Location toLocation(final World world) {
        if (this.cachedLoc == null) {
            this.cachedLoc = new Location(world, this.x, this.y, this.z);
        }
        return this.cachedLoc;
    }
}
