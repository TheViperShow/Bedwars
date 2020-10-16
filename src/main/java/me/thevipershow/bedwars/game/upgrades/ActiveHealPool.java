package me.thevipershow.bedwars.game.upgrades;

import java.util.Iterator;
import java.util.LinkedList;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.HealPoolUpgrade;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class ActiveHealPool {

    private final ActiveGame activeGame;
    private final BedwarsTeam ownerTeam;
    private final HealPoolUpgrade healPoolUpgrade;
    private BukkitTask task = null;
    private Location spawnLoc = null;

    public ActiveHealPool(final ActiveGame activeGame, final BedwarsTeam ownerTeam, HealPoolUpgrade healPoolUpgrade) {
        this.activeGame = activeGame;
        this.ownerTeam = ownerTeam;
        this.healPoolUpgrade = healPoolUpgrade;
    }

    public final void start() {
        final Plugin plugin = activeGame.getPlugin();

        if (spawnLoc == null) {
            activeGame.getBedwarsGame().getMapSpawns()
                    .stream()
                    .filter(v -> v.getBedwarsTeam() == ownerTeam)
                    .findAny()
                    .ifPresent(spawn -> this.spawnLoc = spawn.toLocation(activeGame.getAssociatedWorld()));
        }

        this.task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (this.spawnLoc != null) {
                activeGame.getAssociatedWorld()
                        .getNearbyEntities(spawnLoc, healPoolUpgrade.getHealRadius(), healPoolUpgrade.getHealRadius(), healPoolUpgrade.getHealRadius())
                        .stream()
                        .filter(e -> e instanceof Player)
                        .map(p -> (Player) p)
                        .filter(p -> activeGame.getPlayerTeam(p) == ownerTeam && !activeGame.isOutOfGame(p))
                        .forEach(p -> {
                            final double newHealth = p.getHealth() + (healPoolUpgrade.getHealAmount() * 2);
                            if (newHealth <= 20.00) {
                                p.setHealth(p.getHealth() + healPoolUpgrade.getHealAmount());
                                new HealAnimation(p, activeGame.getPlugin()).start();
                            }
                        });
            }
        }, 1L, 20L * healPoolUpgrade.getHealFrequency());

    }

    private static class HealAnimation {

        private final Player target;
        private final World w;
        private final Plugin plugin;
        private final Iterator<Location> animation;
        private final static double radius = 1.10125d;

        private BukkitTask task = null;

        public HealAnimation(final Player target, final Plugin plugin) {
            this.target = target;
            this.plugin = plugin;
            this.w = target.getWorld();
            final Location startingLocation = target.getLocation().add(0, 0.15, 0);
            final LinkedList<Location> locations = new LinkedList<>();
            double increaseY = 0.00d;

            for (double d = 0d; d < 360d; d += 12.0, increaseY += 0.0625325) {
                final Location modified = new Location(w,
                        (startingLocation.getX() + (radius * Math.sin(d))),
                        increaseY + startingLocation.getY(),
                        (startingLocation.getZ() + (radius * Math.cos(d)))
                );
                locations.add(modified);
            }

            this.animation = locations.iterator();
        }

        public final void start() {
            this.task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                if (!target.isOnline() || !animation.hasNext()) {
                    task.cancel();
                } else {
                    final Location particleLoc = animation.next();
                    w.playEffect(particleLoc, Effect.HEART, 0);
                }
            }, 1L, 1L);
        }

    }

    public final void stop() {
        if (task != null) {
            task.cancel();
        }
    }
}
