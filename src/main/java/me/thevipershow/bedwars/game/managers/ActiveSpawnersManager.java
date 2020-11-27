package me.thevipershow.bedwars.game.managers;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import static me.thevipershow.bedwars.Bedwars.plugin;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.bedwars.objects.spawners.SpawnerType;
import me.thevipershow.bedwars.config.objects.SpawnPosition;
import me.thevipershow.bedwars.config.objects.Spawner;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.spawners.ActiveSpawner;
import org.bukkit.scheduler.BukkitTask;

public final class ActiveSpawnersManager extends AbstractGameManager {

    private final HashSet<ActiveSpawner> activeSpawners = new HashSet<>();
    private final HashSet<BukkitTask> announcementsTasks = new HashSet<>();
    private final HashSet<BukkitTask> emeraldBoostsTasks = new HashSet<>();
    private ActiveSpawner emeraldSampleSpawner, diamondSampleSpawner;

    public ActiveSpawnersManager(ActiveGame activeGame) {
        super(activeGame);
    }

    public final void cancelAllSpawners() {
        this.activeSpawners.forEach(ActiveSpawner::despawn);
    }

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
            activeSpawner.getSpawner().getSpawnerLevels().stream().filter(lvl -> lvl.getLevel() != 1).forEach(i -> announcementsTasks.add(plugin.getServer().getScheduler().runTaskLater(plugin, () -> activeGame.getCachedGameData().getGame().getPlayers().forEach(p -> p.sendMessage(AllStrings.PREFIX.get() + message + i.getLevel())), i.getAfterSeconds() * 20L)));
        }
    }

    public final void createAnnouncements() {
        newAnnouncement(emeraldSampleSpawner, AllStrings.EMERALD_UPGRADE.get());
        newAnnouncement(diamondSampleSpawner, AllStrings.DIAMOND_UPGRADE.get());
    }

    public final void cancelAnnouncements() {
        announcementsTasks.forEach(BukkitTask::cancel);
    }

    private final Map<BedwarsTeam, Collection<ActiveSpawner>> cachedTeamSpawners = new EnumMap<>(BedwarsTeam.class);

    public final Collection<ActiveSpawner> getTeamSpawners(BedwarsTeam bedwarsTeam) {
        if (cachedTeamSpawners.containsKey(bedwarsTeam)) {
            return cachedTeamSpawners.get(bedwarsTeam);
        } else {
            ActiveSpawner nearestGold = findNearest(bedwarsTeam, SpawnerType.GOLD), nearestIron = findNearest(bedwarsTeam, SpawnerType.IRON);
            final Set<ActiveSpawner> set = ImmutableSet.of(nearestIron, nearestGold);
            this.cachedTeamSpawners.put(bedwarsTeam, set);
            return set;
        }
    }

    public ActiveSpawner findNearest(BedwarsTeam bedwarsTeam, SpawnerType spawnerType) {
        ActiveSpawner nearest = null;
        final SpawnPosition spawnLoc = activeGame.getCachedGameData().getCachedTeamSpawnPositions().get(bedwarsTeam);
        double lastDistance = 0D;
        for (final ActiveSpawner activeSpawner : this.activeSpawners) {
            final double tempSquaredDistance = activeSpawner.getSpawner().getSpawnPosition().squaredDistance(spawnLoc);
            if (nearest == null || tempSquaredDistance < lastDistance) {
                nearest = activeSpawner;
                lastDistance = tempSquaredDistance;
            }
        }
        return nearest;
    }

    public final HashSet<ActiveSpawner> getActiveSpawners() {
        return activeSpawners;
    }

    public final HashSet<BukkitTask> getAnnouncementsTasks() {
        return announcementsTasks;
    }

    public final ActiveSpawner getEmeraldSampleSpawner() {
        return emeraldSampleSpawner;
    }

    public final void setEmeraldSampleSpawner(ActiveSpawner emeraldSampleSpawner) {
        this.emeraldSampleSpawner = emeraldSampleSpawner;
    }

    public final ActiveSpawner getDiamondSampleSpawner() {
        return diamondSampleSpawner;
    }

    public final HashSet<BukkitTask> getEmeraldBoostsTasks() {
        return emeraldBoostsTasks;
    }

    public final void setDiamondSampleSpawner(ActiveSpawner diamondSampleSpawner) {
        this.diamondSampleSpawner = diamondSampleSpawner;
    }
}
