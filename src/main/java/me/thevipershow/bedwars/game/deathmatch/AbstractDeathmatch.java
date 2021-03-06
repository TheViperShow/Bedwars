package me.thevipershow.bedwars.game.deathmatch;

import java.util.Map;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.api.AbstractDeathmatchStartEvent;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.SpawnPosition;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeType;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveGameState;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import me.thevipershow.bedwars.game.managers.BedManager;
import me.thevipershow.bedwars.game.managers.TeamManager;
import me.thevipershow.bedwars.listeners.unregisterable.DragonRedirectorListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.material.Bed;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbstractDeathmatch {

    protected final ActiveGame activeGame;
    protected final DragonRedirectorListener dragonTargetListener;
    protected final int startAfter;

    protected long startTime = -1L;
    protected boolean running = false;
    protected BukkitTask task = null;

    public AbstractDeathmatch(final ActiveGame activeGame) {
        this.activeGame = activeGame;
        this.startAfter = activeGame.getBedwarsGame().getDeathmatchStart();
        this.dragonTargetListener = new DragonRedirectorListener(activeGame);
    }

    public final int numberOfDragonsToSpawn(final BedwarsTeam team) {
        for (Map.Entry<BedwarsTeam, ? extends TeamData<?>> entry : activeGame.getTeamManager().getDataMap().entrySet()) {
            BedwarsTeam b = entry.getKey();
            TeamData<?> data = entry.getValue();
            if (b != team) {
                continue;
            }
            if (data.getUpgradeLevel(UpgradeType.DRAGON_BUFF) == 0) {
                return 1;
            } else {
                return 2;
            }
        }
        return 1;
    }

    public final void spawnDragon(final BedwarsTeam bedwarsTeam) {
        final int toSpawn = numberOfDragonsToSpawn(bedwarsTeam);
        int spawned = 0;
        while (spawned < toSpawn) {
            final SpawnPosition teamSpawn = activeGame.getBedwarsGame().spawnPosOfTeam(bedwarsTeam);
            final EnderDragon enderDragon = (EnderDragon) activeGame.getCachedGameData().getGame().spawnEntity(teamSpawn.toLocation(activeGame.getCachedGameData().getGame()).add(0.0, 25.0, 0.0), EntityType.ENDER_DRAGON);
            enderDragon.setRemoveWhenFarAway(false);
            dragonTargetListener.getDragonTeamsMap().put(enderDragon.getUniqueId(), bedwarsTeam);
            spawned++;
        }
    }

    public abstract void spawnEnderdragons();

    public void announceDeathmatch() {
        activeGame.getTeamManager().getDataMap().values().forEach(v -> v.perform(b -> b.sendMessage(AllStrings.PREFIX.get() + AllStrings.ANNOUNCE_DEATHMATCH.get())));
    }

    public abstract void startDeathMatch();

    /**
     * Break all of the beds :)
     */
    public final void breakBeds() {
        BedManager bedManager = activeGame.getBedManager();
        TeamManager<?> teamManager = activeGame.getTeamManager();
        teamManager.getDataMap().keySet().forEach(bedManager::destroyBed);
    }

    public final void start() {
        this.task = activeGame.getPlugin().getServer().getScheduler().runTaskLater(activeGame.getPlugin(), () -> {
            AbstractDeathmatchStartEvent event = new AbstractDeathmatchStartEvent(activeGame);
            activeGame.callGameEvent(event);
            if (!event.isCancelled()) {
                setRunning(true);
                this.startTime = System.currentTimeMillis();
                startDeathMatch();
                breakBeds();
                activeGame.getPlugin().getServer().getPluginManager().registerEvents(dragonTargetListener, activeGame.getPlugin());
            }
        }, startAfter * 20L);
    }

    public final void stop() {
        this.running = false;
        if (task != null) {
            task.cancel();
        }
        dragonTargetListener.getDragonTeamsMap().clear();
        dragonTargetListener.unregister();
    }

    public final long getStartTime() {
        return startTime;
    }

    public final long timeUntilDeathmatch() {
        if (activeGame.getGameState() == ActiveGameState.STARTED) {
            return ((activeGame.getStartTime() / 1000L) + startAfter) - (System.currentTimeMillis() / 1000L);
        }
        return -1L;
    }

    public final long timeUntilDragons() {
        if (activeGame.getGameState() == ActiveGameState.STARTED) {
            return ((activeGame.getStartTime() / 1000L) + startAfter + 600) - (System.currentTimeMillis() / 1000L);
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
