package me.thevipershow.bedwars.game.objects;

import java.util.HashSet;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import static me.thevipershow.bedwars.Bedwars.plugin;
import me.thevipershow.bedwars.bedwars.objects.spawners.SpawnerType;
import me.thevipershow.bedwars.config.objects.Spawner;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveSpawner;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;

public final class ActiveSpawnersManager {

    public ActiveSpawnersManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private final ActiveGame activeGame;

    private final HashSet<ActiveSpawner> activeSpawners = new HashSet<>();
    private final HashSet<BukkitTask> announcementsTasks = new HashSet<>();
    private ActiveSpawner emeraldSampleSpawner, diamondSampleSpawner;

    public final void addSpawners() {
        for (Spawner spawner : activeGame.getBedwarsGame().getSpawners()) {
            ActiveSpawner activeSpawner = new ActiveSpawner(spawner, activeGame);

            if (diamondSampleSpawner == null && spawner.getSpawnerType() == SpawnerType.DIAMOND) {
                diamondSampleSpawner = activeSpawner;
            }
            if (emeraldSampleSpawner == null && spawner.getSpawnerType() == SpawnerType.EMERALD) {
                emeraldSampleSpawner = activeSpawner;
            }

            activeSpawners.add(activeSpawner);
        }
    }

    public final void spawnAll() {
        for (ActiveSpawner spawner : activeSpawners) {
            spawner.spawn();
        }
    }

    private void newAnnouncement(ActiveSpawner activeSpawner, String message) {
        if (activeSpawner != null) {
            activeSpawner.getSpawner().getSpawnerLevels().stream().filter(lvl -> lvl.getLevel() != 1).forEach(i -> announcementsTasks.add(plugin.getServer().getScheduler().runTaskLater(plugin, () -> activeGame.getCachedGameData().getGame().getPlayers().forEach(p -> p.sendMessage(Bedwars.PREFIX + message + i.getLevel())), i.getAfterSeconds() * 20L)));
        }
    }

    public final void createAnnouncements() {
        newAnnouncement(emeraldSampleSpawner, AllStrings.EMERALD_UPGRADE.get());
        newAnnouncement(diamondSampleSpawner, AllStrings.DIAMOND_UPGRADE.get());
    }

    public final void cancelAnnouncements() {
        announcementsTasks.forEach(BukkitTask::cancel);
    }

    public ActiveGame getActiveGame() {
        return activeGame;
    }

    public HashSet<ActiveSpawner> getActiveSpawners() {
        return activeSpawners;
    }

    public HashSet<BukkitTask> getAnnouncementsTasks() {
        return announcementsTasks;
    }

    public ActiveSpawner getEmeraldSampleSpawner() {
        return emeraldSampleSpawner;
    }

    public void setEmeraldSampleSpawner(ActiveSpawner emeraldSampleSpawner) {
        this.emeraldSampleSpawner = emeraldSampleSpawner;
    }

    public ActiveSpawner getDiamondSampleSpawner() {
        return diamondSampleSpawner;
    }

    public void setDiamondSampleSpawner(ActiveSpawner diamondSampleSpawner) {
        this.diamondSampleSpawner = diamondSampleSpawner;
    }
}
