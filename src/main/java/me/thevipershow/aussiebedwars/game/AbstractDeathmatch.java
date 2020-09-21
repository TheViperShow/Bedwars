package me.thevipershow.aussiebedwars.game;

import java.util.Collections;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.SpawnPosition;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.UpgradeType;
import me.thevipershow.aussiebedwars.listeners.game.DragonTargetListener;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderDragon;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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

    public final int numberOfDragonsToSpawn(final BedwarsTeam team) {
        return activeGame.getUpgradesLevelsMap().get(UpgradeType.DRAGON_BUFF).get(team) > 0 ? 2 : 1;
    }

    public final void spawnDragon(final BedwarsTeam bedwarsTeam) {
        final int toSpawn = numberOfDragonsToSpawn(bedwarsTeam);
        int spawned = 0;
        while (spawned < toSpawn) {
            final SpawnPosition teamSpawn = activeGame.getBedwarsGame().spawnPosOfTeam(bedwarsTeam);
            final EnderDragon enderDragon = (EnderDragon) activeGame.associatedWorld.spawnEntity(teamSpawn.toLocation(activeGame.getAssociatedWorld()).add(0.0, 30.0, 0.0), EntityType.ENDER_DRAGON);
            enderDragon.setRemoveWhenFarAway(false);
            final CraftEnderDragon coolDragon = ((CraftEnderDragon) enderDragon);
            dragonTargetListener.getDragonPlayerMap().put(coolDragon, bedwarsTeam);
            spawned++;
        }
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
