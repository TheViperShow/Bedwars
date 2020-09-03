package me.thevipershow.aussiebedwars.bedwars.objects.spawners.animations;

import java.util.List;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbstractAnimation<T> {

    protected final CacheableFunction<T> cacheableFunction;
    protected boolean isRunning = false;
    protected BukkitTask runningTask = null;
    protected final List<T> list;
    protected final T startingPosition;

    protected AbstractAnimation(final CacheableFunction<T> cacheableFunction, final T t) {
        this.cacheableFunction = cacheableFunction;
        this.startingPosition = t;
        list = cacheableFunction.cacheResult();
    }

    public abstract void startAnimation();

    public abstract void stopAnimation();

    public CacheableFunction<T> getCacheableFunction() {
        return cacheableFunction;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public BukkitTask getRunningTask() {
        return runningTask;
    }

    public void setRunningTask(BukkitTask runningTask) {
        this.runningTask = runningTask;
    }

    public List<T> getList() {
        return list;
    }
}
