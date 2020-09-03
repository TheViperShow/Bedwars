package me.thevipershow.aussiebedwars.bedwars.objects.spawners.animations;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;

public final class CachedSinusoidalWave extends CachedLocationFunction {
    public CachedSinusoidalWave(Location startingPosition) {
        super(startingPosition);
    }

    @Override
    public List<Location> cacheResult() {
        final LinkedList<Location> values = new LinkedList<>();
        for (double d = -1.000d; d <= 1.000d; d += 0.100d) {
            values.offerLast(super.startingPosition.add(0.00d, 0.14d * Math.sin(d), 0.00d));
        }
        for (double d = 1.000d; d >= -1.000d; d -= 0.100d) {
            values.offerLast(super.startingPosition.add(0.00d, 0.14d * Math.sin(d), 0.00d));
        }
        return values;
    }
}
