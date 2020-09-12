package me.thevipershow.aussiebedwars.listeners.game;

import java.util.Set;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.aussiebedwars.events.TeamBedDestroyEvent;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player p = event.getPlayer();
        final Block b = event.getBlock();
        if (!b.getWorld().equals(activeGame.getAssociatedWorld())) return;


        if (activeGame.getPlayerPlacedBlocks().contains(b)) {
            activeGame.getPlayerPlacedBlocks().remove(b);
        } else {
            if (b.getType() == Material.BED_BLOCK) {
                System.out.println("1");
                final BedwarsTeam playerTeam = activeGame.getPlayerTeam(p);
                final BedwarsTeam destroyedBedTeam = teamOfBed(b);
                if (playerTeam == destroyedBedTeam) {
                    p.sendMessage(AussieBedwars.PREFIX + "§eYou cannot destroy your own bed.");
                    event.setCancelled(true);
                } else {
                    final TeamBedDestroyEvent e = new TeamBedDestroyEvent(activeGame, destroyedBedTeam);
                    activeGame.getPlugin().getServer().getPluginManager().callEvent(e);

                    if (e.isCancelled()) return;

                    activeGame.getDestroyedTeams().add(destroyedBedTeam);
                    activeGame.destroyTeamBed(destroyedBedTeam);
                }
            } else {
                event.setCancelled(true);
            }
        }

        System.out.println("2");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemSpawn(final ItemSpawnEvent event) {
        if (!event.getEntity().getWorld().equals(activeGame.getAssociatedWorld())) return;
        if (event.getEntity().getItemStack().getType() == Material.BED) {
            event.setCancelled(true);
        }
    }
}