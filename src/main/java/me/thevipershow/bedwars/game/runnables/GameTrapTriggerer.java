package me.thevipershow.bedwars.game.runnables;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import me.thevipershow.bedwars.api.TrapTriggerEvent;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.SpawnPosition;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveGameState;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.game.enums.PlayerState;
import me.thevipershow.bedwars.game.managers.TeamManager;
import me.thevipershow.bedwars.game.managers.TrapsManager;
import me.thevipershow.bedwars.game.upgrades.traps.ActiveTrap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

public final class GameTrapTriggerer {

    private final ActiveGame activeGame;
    private final Runnable gameTrapTriggererRunnable;
    private BukkitTask task = null;
    private final static long ONE_SECOND = 20L;
    private final static float TRAP_ACTIVATION_DELAY_SECONDS = 15.0f;
    private final static float BOUNDING_BOX_X = 20f, BOUNDING_BOX_Z = 20f, BOUNDING_BOX_Y = 5.01f;

    private static boolean isValidTrapTarget(BedwarsPlayer bedwarsPlayer) {
        return bedwarsPlayer.isOnline() && !bedwarsPlayer.isImmuneToTraps() && bedwarsPlayer.getPlayerState() != PlayerState.DEAD && bedwarsPlayer.getPlayerState() != PlayerState.RESPAWNING;
    }

    @Nullable
    private BedwarsTeam hasEnteredIslandBoundingBox(BedwarsPlayer bedwarsPlayer) {
        Location currentPlayerLocation = bedwarsPlayer.getLocation();
        BedwarsTeam playerTeam = bedwarsPlayer.getBedwarsTeam();
        Map<BedwarsTeam, SpawnPosition> teamSpawnPositionMap = activeGame.getCachedGameData().getCachedTeamSpawnPositions();

        for (Map.Entry<BedwarsTeam, SpawnPosition> entry : teamSpawnPositionMap.entrySet()) {
            BedwarsTeam ownerTeam = entry.getKey();
            SpawnPosition teamPos = entry.getValue();
            if (playerTeam == ownerTeam) {
                continue;
            }

            double xDistance = teamPos.xDistance(currentPlayerLocation);
            double zDistance = teamPos.zDistance(currentPlayerLocation);
            double yDistance = teamPos.yDistance(currentPlayerLocation);
            if (xDistance <= BOUNDING_BOX_X && yDistance <= BOUNDING_BOX_Y && zDistance <= BOUNDING_BOX_Z) {
                return ownerTeam;
            }
        }

        return null;
    }

    private boolean enoughTimePassedSinceLastActivation(BedwarsTeam team) {
        final long lastTeamTrapActivation = activeGame.getTrapsManager().getTrapsActivationTime().get(team); // getting the last time that
        // a trap owned by this team
        // has been activated.

        // 15.0 seconds have not passed
        return (System.currentTimeMillis() - lastTeamTrapActivation) / 1e3 >= TRAP_ACTIVATION_DELAY_SECONDS;
    }

    private void callEventAndProceedWhenUncancelled(BedwarsPlayer bedwarsPlayer, ActiveTrap first, Runnable action) {
        TrapTriggerEvent trapTriggerEvent = new TrapTriggerEvent(activeGame, bedwarsPlayer, first, bedwarsPlayer.getBedwarsTeam());
        activeGame.callGameEvent(trapTriggerEvent);
        if (!trapTriggerEvent.isCancelled()) {
            action.run();
        }
    }

    private void updateTrapsGUI(TeamManager<?> teamManager, BedwarsTeam activatedTeamTrap, List<ActiveTrap> enemyTraps) {
        teamManager.dataOfTeam(activatedTeamTrap)
                .perform(p -> activeGame.getGameInventoriesManager().getAssociatedTrapsGUI().get(p.getUniqueId()).setItem(30 + enemyTraps.size(), new ItemStack(Material.STAINED_GLASS_PANE, 1 + enemyTraps.size())));
    }

    private void activateTrapLogic(Map<BedwarsTeam, LinkedList<ActiveTrap>> activeTrapMap, BedwarsTeam activatedTeamTrap, BedwarsPlayer triggerCause, TrapsManager trapsManager, TeamManager<?> teamManager) {
        final LinkedList<ActiveTrap> enemyTraps = activeTrapMap.get(activatedTeamTrap);
        if (enemyTraps != null) {
            ActiveTrap first = enemyTraps.pollFirst();
            callEventAndProceedWhenUncancelled(triggerCause, first, () -> {
                first.trigger(triggerCause);
                trapsManager.getTrapsActivationTime().put(activatedTeamTrap, System.currentTimeMillis());
                this.updateTrapsGUI(teamManager, activatedTeamTrap, enemyTraps);
            });
        }
    }

    private Consumer<? super BedwarsPlayer> checkTrapLogicForAll(TeamManager<?> teamManager, Map<BedwarsTeam, LinkedList<ActiveTrap>> activeTrapMap, TrapsManager trapsManager) {
        return bedwarsPlayer -> {
            BedwarsTeam activatedTeamTrap = this.hasEnteredIslandBoundingBox(bedwarsPlayer);
            boolean isImmuneToTraps = bedwarsPlayer.isImmuneToTraps();
            if (activatedTeamTrap == null && isImmuneToTraps) {
                bedwarsPlayer.setImmuneToTraps(false);
            } else if (activatedTeamTrap != null && !isImmuneToTraps && isValidTrapTarget(bedwarsPlayer) && enoughTimePassedSinceLastActivation(activatedTeamTrap)) {
                this.activateTrapLogic(activeTrapMap, activatedTeamTrap, bedwarsPlayer, trapsManager, teamManager);
            }
        };
    }

    private Runnable getGameTrapTriggererRunnable() {
        return () -> {
            if (activeGame.getGameState() != ActiveGameState.STARTED) {
                this.stop();
            }

            TrapsManager trapsManager = activeGame.getTrapsManager();
            Map<BedwarsTeam, LinkedList<ActiveTrap>> activeTrapMap = trapsManager.getActiveTraps();

            if (activeTrapMap.isEmpty()) {
                return;
            }

            TeamManager<?> teamManager = activeGame.getTeamManager();

            teamManager.performAll(this.checkTrapLogicForAll(teamManager, activeTrapMap, trapsManager));
        };
    }

    public GameTrapTriggerer(final ActiveGame activeGame) {
        this.activeGame = activeGame;
        this.gameTrapTriggererRunnable = getGameTrapTriggererRunnable();
    }

    private BukkitTask generateTask() {
        Plugin plugin = activeGame.getPlugin();
        return plugin.getServer().getScheduler().runTaskTimer(plugin, this.gameTrapTriggererRunnable, ONE_SECOND, ONE_SECOND);
    }

    public final void start() {
        if (task != null) {
            return;
        }
        this.task = generateTask();
    }

    public final void stop() {
        if (task != null) {
            task.cancel();
        }
    }
}
