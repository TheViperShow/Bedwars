package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.bedwars.events.TeamBedDestroyEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

public final class BedBreakListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public BedBreakListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private BedwarsTeam teamOfBed(final Block block) {
        final Location loc = block.getLocation();
        BedwarsTeam nearestTeam = null;
        double squaredDistance = -1;
        for (final TeamSpawnPosition spawn : activeGame.getBedwarsGame().getMapSpawns()) {
            final double tempSqdDist = spawn.squaredDistance(loc);
            if (nearestTeam == null) {
                nearestTeam = spawn.getBedwarsTeam();
                squaredDistance = tempSqdDist;
                continue;
            }

            if (tempSqdDist < squaredDistance) {
                nearestTeam = spawn.getBedwarsTeam();
                squaredDistance = tempSqdDist;
            }
        }
        return nearestTeam;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player p = event.getPlayer();
        final Block b = event.getBlock();

        if (!activeGame.isHasStarted()) {
            return;
        }

        if (!b.getWorld().equals(activeGame.getAssociatedWorld())) {
            return;
        }

        if (activeGame.getPlayerPlacedBlocks().contains(b)) {
            activeGame.getPlayerPlacedBlocks().remove(b);
        } else {
            if (b.getType() == Material.BED_BLOCK) {

                if (!activeGame.getAbstractDeathmatch().isRunning()) {

                    final BedwarsTeam playerTeam = activeGame.getPlayerTeam(p);
                    final BedwarsTeam destroyedBedTeam = teamOfBed(b);
                    if (playerTeam == destroyedBedTeam) {
                        p.sendMessage(Bedwars.PREFIX + AllStrings.CANNOT_BREAK_OWN_BED.get());
                        event.setCancelled(true);
                    } else if (activeGame.getAssignedTeams().containsKey(destroyedBedTeam)) {
                        final TeamBedDestroyEvent e = new TeamBedDestroyEvent(activeGame, destroyedBedTeam);
                        activeGame.getPlugin().getServer().getPluginManager().callEvent(e);

                        if (e.isCancelled()) return;

                        activeGame.getDestroyedTeams().add(destroyedBedTeam);
                        activeGame.destroyTeamBed(destroyedBedTeam, p);
                    }

                } else {
                    event.setCancelled(true);
                    p.sendMessage(Bedwars.PREFIX + AllStrings.DEATHMATCH_BEDS_CANNOT_BE_BROKEN.get());
                    p.sendMessage(Bedwars.PREFIX + AllStrings.DEATHMATCH_BEDS_CANNOT_BE_BROKEN_2.get());
                }

            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemSpawn(final ItemSpawnEvent event) {
        if (!event.getEntity().getWorld().equals(activeGame.getAssociatedWorld())) return;
        if (event.getEntity().getItemStack().getType() == Material.BED) {
            event.setCancelled(true);
        }
    }
}
