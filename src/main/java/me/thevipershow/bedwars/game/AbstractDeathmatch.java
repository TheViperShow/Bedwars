package me.thevipershow.bedwars.game;

import java.util.Map;
import java.util.Objects;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.SpawnPosition;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeType;
import me.thevipershow.bedwars.listeners.game.DragonTargetListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderDragon;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.material.Bed;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbstractDeathmatch {

    protected final ActiveGame activeGame;
    protected final DragonTargetListener dragonTargetListener;
    protected final int startAfter;

    protected long startTime = -1L;
    protected boolean running = false;
    protected BukkitTask task = null;

    public AbstractDeathmatch(final ActiveGame activeGame) {
        this.activeGame = activeGame;
        this.startAfter = activeGame.getBedwarsGame().getDeathmatchStart();
        this.dragonTargetListener = new DragonTargetListener(activeGame);
    }

    public final int numberOfDragonsToSpawn(final BedwarsTeam team) {
        final Map<BedwarsTeam, Integer> m = this.activeGame.getUpgradesLevelsMap().get(UpgradeType.DRAGON_BUFF);
        final Integer i = Objects.requireNonNull(m.get(team));
        return i == 0 ? 1 : 2;
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

    public void announceDeathmatch() {
        activeGame.getAssociatedQueue().perform(p -> {
            p.sendMessage(Bedwars.PREFIX + AllStrings.ANNOUNCE_DEATHMATCH.get());
        });
    }

    public abstract void startDeathMatch();

    public void breakBeds() {
        activeGame.getBedwarsGame().getBedSpawnPositions().forEach(bed -> {
            final Location location = bed.toLocation(activeGame.getAssociatedWorld());
            final Block block = location.getBlock();
            if (block != null && block.getType() == Material.BED) {
                Bed b = (Bed) block.getState().getData();
                final Block facing = block.getRelative(b.getFacing());
                block.setType(Material.AIR);
                facing.setType(Material.AIR);
            }
        });
    }

    public void start() {
        this.task = activeGame.plugin.getServer().getScheduler().runTaskLater(activeGame.plugin, () -> {
            setRunning(true);
            this.startTime = System.currentTimeMillis();
            startDeathMatch();
            breakBeds();
            activeGame.plugin.getServer().getPluginManager().registerEvents(dragonTargetListener, activeGame.plugin);
        }, startAfter * 20L);
    }

    public void stop() {
        this.running = false;
        if (task != null) task.cancel();
        dragonTargetListener.getDragonPlayerMap().clear();
        dragonTargetListener.unregister();
    }

    public final long getStartTime() {
        return startTime;
    }

    public final long timeUntilDeathmatch() {
        if (activeGame.isHasStarted()) {
            return ((activeGame.getStartTime() / 1000L) + startAfter)  - (System.currentTimeMillis() / 1000L);
        }
        return -1L;
    }

    public final long timeUntilDragons() {
        if (activeGame.isHasStarted()) {
            return ((activeGame.getStartTime() / 1000L) + startAfter + 600)  - (System.currentTimeMillis() / 1000L);
        }
        return -1L;
    }

    public final boolean isRunning() {
        return running;
    }

    public final void setRunning(final boolean running) {
        this.running = running;
    }
}
