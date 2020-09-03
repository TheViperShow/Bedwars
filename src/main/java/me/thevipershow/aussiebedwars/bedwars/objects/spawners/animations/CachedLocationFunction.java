package me.thevipershow.aussiebedwars.bedwars.objects.spawners.animations;

import java.util.List;
import org.bukkit.Location;

public abstract class CachedLocationFunction implements CacheableFunction<Location> {
    protected final Location startingPosition;
    protected final List<Location> cachedData;

    public CachedLocationFunction(Location location) {
        this.startingPosition = location;
        this.cachedData = cacheResult();
    }

    public Location getStartingPosition() {
        return startingPosition;
    }

    public List<Location> getCachedData() {
        return cachedData;
    }
}
