package me.thevipershow.aussiebedwars.bedwars.objects.spawners.animations;

import me.thevipershow.aussiebedwars.bedwars.objects.spawners.SpawnerType;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class ArmorStandAnimation extends AbstractAnimation<Location> {

    private long delay = 1L;
    private final Plugin plugin;
    private final ArmorStandRunnable runnable;
    private BukkitTask task = null;

    public ArmorStandAnimation(CachedLocationFunction cacheableFunction,
                               Plugin plugin,
                               SpawnerType spawnerType,
                               String armorStandName) {
        super(cacheableFunction, cacheableFunction.getStartingPosition());
        this.plugin = plugin;
        this.runnable = new ArmorStandRunnable(super.getList(), spawnerType, armorStandName, plugin);
    }

    @Override
    public void startAnimation() {
        if (isRunning)
            throw new UnsupportedOperationException("Tried to start an animation while already running one.");
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, 0, delay);
        isRunning = true;
    }

    @Override
    public void stopAnimation() {
        if (isRunning) throw new UnsupportedOperationException("Tried to stop an animation without running one.");
        isRunning = false;
    }

    public long getDelay() {
        return delay;
    }

    public Location getCurrentArmorStandLocation() {
        return getArmorStand().getLocation();
    }

    public ArmorStand getArmorStand() {
        return getRunnable().getArmorStand();
    }

    public ArmorStandRunnable getRunnable() {
        return runnable;
    }

    public BukkitTask getTask() {
        return task;
    }
}
