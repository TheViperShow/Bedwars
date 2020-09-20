package me.thevipershow.aussiebedwars.game;

import me.thevipershow.aussiebedwars.listeners.game.DragonTargetListener;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbstractDeathmatch {

    protected final ActiveGame activeGame;
    protected final DragonTargetListener dragonTargetListener;
    protected final int startAfter;

    protected boolean running = false;
    protected BukkitTask task = null;

    public AbstractDeathmatch(final ActiveGame activeGame) {
        this.activeGame = activeGame;
        this.startAfter = activeGame.getBedwarsGame().getDeathmatchStart();
        this.dragonTargetListener = new DragonTargetListener(activeGame);
    }

    public abstract void spawnEnderdragons();

    public abstract void announceDeathmatch();

    public abstract void startDeathMatch();

    public void start() {
        this.task = activeGame.plugin.getServer().getScheduler().runTaskLater(activeGame.plugin, () -> {
            setRunning(true);
            startDeathMatch();
            activeGame.plugin.getServer().getPluginManager().registerEvents(dragonTargetListener, activeGame.plugin);
        }, startAfter * 20L);
    }

    public void stop() {
        this.running = false;
        if (task != null) task.cancel();
        dragonTargetListener.getDragonPlayerMap().clear();
        dragonTargetListener.unregister();
    }

    public final boolean isRunning() {
        return running;
    }

    public final void setRunning(final boolean running) {
        this.running = running;
    }
}
