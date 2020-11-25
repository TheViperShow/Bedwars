package me.thevipershow.bedwars.game.runnables;

import java.util.LinkedList;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.SpawnPosition;
import me.thevipershow.bedwars.api.TrapTriggerEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveGameState;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.game.enums.PlayerState;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import me.thevipershow.bedwars.game.upgrades.traps.ActiveTrap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public final class GameTrapTriggerer {

    private final ActiveGame activeGame;
    private BukkitTask task = null;

    public GameTrapTriggerer(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    public final void start() {
        if (task != null) {
            return;
        }

        task = activeGame.getPlugin().getServer().getScheduler().runTaskTimer(activeGame.getPlugin(), () -> {

            if (activeGame.getGameState() != ActiveGameState.STARTED) {
                this.stop();
            }

            teamLabel:
            for (final Map.Entry<BedwarsTeam, LinkedList<ActiveTrap>> teamActiveTraps : activeGame.getTrapsManager().getActiveTraps().entrySet()) {
                final LinkedList<ActiveTrap> traps = teamActiveTraps.getValue();  // Getting all active traps lists
                if (traps.isEmpty()) {                                            // and looping through each one of them
                    continue;
                }

                final BedwarsTeam trapTeamOwner = teamActiveTraps.getKey();
                for (final Map.Entry<BedwarsTeam, ? extends TeamData<?>> entry : activeGame.getTeamManager().getDataMap().entrySet()) {
                    final BedwarsTeam bedwarsTeam = entry.getKey();
                    if (trapTeamOwner != bedwarsTeam) {
                        for (BedwarsPlayer bedwarsPlayer : entry.getValue().getAll()) {


                            if (bedwarsPlayer.isOnline() && bedwarsPlayer.getPlayerState() != PlayerState.DEAD && bedwarsPlayer.getPlayerState() != PlayerState.RESPAWNING) {

                                final SpawnPosition spawnPos = activeGame.getCachedGameData().getCachedTeamSpawnPositions().get(trapTeamOwner);
                                final Location pLoc = bedwarsPlayer.getLocation();

                                final double xDist = spawnPos.xDistance(pLoc);
                                final double zDist = spawnPos.zDistance(pLoc);
                                final double yDist = spawnPos.yDistance(pLoc);

                                final boolean isPlayerImmune = bedwarsPlayer.isImmuneToTraps(); // Checking if the player
                                // is still immune to traps

                                final long lastTeamTrapActivation = activeGame.getTrapsManager().getTrapsActivationTime().get(bedwarsTeam); // getting the last time that
                                // a trap owned by this team
                                // has been activated.

                                if ((System.currentTimeMillis() - lastTeamTrapActivation) / 1e3 < 15.0) { // 15.0 seconds have not passed
                                    continue teamLabel;                                             // skipping this team checks.
                                }

                                if (xDist <= 20.0 && zDist <= 20.0 && yDist <= 5.01) { // Player has entered island's
                                    // box region [±20.0, ±6.01, ±20.0]

                                    if (isPlayerImmune) { // We should not trigger trap
                                        continue;         // when player is immune
                                    }

                                    final TrapTriggerEvent trapTriggerEvent = new TrapTriggerEvent(activeGame, bedwarsPlayer, traps.getFirst(), trapTeamOwner);
                                    activeGame.getPlugin().getServer().getPluginManager().callEvent(trapTriggerEvent);
                                    if (trapTriggerEvent.isCancelled()) {
                                        return;
                                    }

                                    teamActiveTraps.getValue().pollFirst().trigger(bedwarsPlayer); // removing

                                    // Updating last activation time:
                                    activeGame.getTrapsManager().getTrapsActivationTime().compute(trapTeamOwner, (k, v) -> v = System.currentTimeMillis());

                                    // Updating team GUIs: TODO: Reimplement for refactoring
                                    activeGame.getTeamManager().dataOfTeam(trapTeamOwner)
                                            .perform(p -> activeGame.getGameInventoriesManager().getAssociatedTrapsGUI().get(p.getUniqueId()).setItem(30 + teamActiveTraps.getValue().size(), new ItemStack(Material.STAINED_GLASS_PANE, 1 + teamActiveTraps.getValue().size())));

                                    //activeGame.getTeamPlayers(trapTeamOwner)
                                    //        .forEach(p -> activeGame.getAssociatedUpgradeGUI().get(p.getUniqueId()).setItem(30 + teamActiveTraps.getValue().size(), new ItemStack(Material.STAINED_GLASS_PANE, 1 + teamActiveTraps.getValue().size())));

                                    continue teamLabel; // skipping to next team's traps

                                } else { // here the player is out of the island box
                                    if (isPlayerImmune) {                                // Removing the player from
                                        bedwarsPlayer.setImmuneToTraps(false);           // the immune status, because he left island.
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, 20L, 20L);
    }

    public final void stop() {
        if (task != null) {
            task.cancel();
        }
    }
}
