package me.thevipershow.bedwars.listeners.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.SpawnPosition;
import me.thevipershow.bedwars.events.TrapTriggerEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.upgrades.ActiveTrap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

        task = activeGame.getPlugin().getServer().getScheduler()
                .runTaskTimer(activeGame.getPlugin(), () -> {
                    teamTrapsLabel:
                    for (final Map.Entry<BedwarsTeam, LinkedList<ActiveTrap>> teamActiveTraps : activeGame.getTeamActiveTrapsList().entrySet()) {
                        final LinkedList<ActiveTrap> traps = teamActiveTraps.getValue();  // Getting all active traps lists
                        if (traps.isEmpty()) {                                            // and looping through each one of them
                            continue;
                        }
                        final BedwarsTeam trapTeamOwner = teamActiveTraps.getKey();

                        for (final Map.Entry<BedwarsTeam, List<Player>> assignedTeams : activeGame.getAssignedTeams().entrySet()) {
                            if (assignedTeams.getKey() != trapTeamOwner) { // player from own team cannot trigger their traps. :-)

                                for (final Player runner : assignedTeams.getValue()) {
                                    if (runner.isOnline() && !activeGame.isOutOfGame(runner) && !activeGame.getPlayersRespawning().contains(runner)) { // Ignoring all players that
                                                                                                // are either out of game or left.
                                        final SpawnPosition spawnPos = activeGame.getTeamSpawn(trapTeamOwner);
                                        final Location pLoc = runner.getLocation();

                                        final double xDist = spawnPos.xDistance(pLoc);
                                        final double zDist = spawnPos.zDistance(pLoc);
                                        final double yDist = spawnPos.yDistance(pLoc);

                                        final boolean isPlayerImmune = activeGame.getImmuneTrapPlayers().contains(runner); // Checking if the player
                                                                                                                           // is still immune to traps

                                        final long lastTeamTrapActivation = activeGame.getLastActivatedTraps().get(assignedTeams.getKey()); // getting the last time that
                                                                                                                                            // a trap owned by this team
                                                                                                                                            // has been activated.

                                        if ((System.currentTimeMillis() - lastTeamTrapActivation) / 1e+3 < 15.0) { // 15.0 seconds have not passed
                                            continue teamTrapsLabel;                                              // skipping this team checks.
                                        }

                                        if (xDist <= 20.0 && zDist <= 20.0 && yDist <= 5.01) { // Player has entered island's
                                                                                               // box region [±20.0, ±6.01, ±20.0]

                                            if (isPlayerImmune) { // We should not trigger trap
                                                continue;         // when player is immune
                                            }

                                            final TrapTriggerEvent trapTriggerEvent = new TrapTriggerEvent(runner, traps.getFirst(), trapTeamOwner);
                                            activeGame.getPlugin().getServer().getPluginManager().callEvent(trapTriggerEvent);
                                            if (trapTriggerEvent.isCancelled()) {
                                                return;
                                            }

                                            teamActiveTraps.getValue().pollFirst().trigger(runner); // removing

                                            // Updating last activation time:
                                            activeGame.getLastActivatedTraps().compute(trapTeamOwner, (k,v) -> v = System.currentTimeMillis());

                                            // Updating team GUIs:
                                            activeGame.getTeamPlayers(trapTeamOwner).forEach(p -> activeGame.getAssociatedTrapsGUI().get(p.getUniqueId()).setItem(30 + teamActiveTraps.getValue().size(), new ItemStack(Material.STAINED_GLASS_PANE, 1 + teamActiveTraps.getValue().size())));
                                            activeGame.getTeamPlayers(trapTeamOwner).forEach(p -> activeGame.getAssociatedUpgradeGUI().get(p.getUniqueId()).setItem(30 + teamActiveTraps.getValue().size(), new ItemStack(Material.STAINED_GLASS_PANE, 1 + teamActiveTraps.getValue().size())));

                                            continue teamTrapsLabel; // skipping to next team's traps

                                        } else { // here the player is out of the island box
                                            if (isPlayerImmune) {                                   // Removing the player from
                                                activeGame.getImmuneTrapPlayers().remove(runner);   // the immune list, because he left island.
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
