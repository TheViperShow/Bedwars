package me.thevipershow.aussiebedwars.bedwars.objects.spawners.animations;

import java.util.List;

@FunctionalInterface
public interface CacheableFunction<T> {
    List<T> cacheResult();
}
