package me.thevipershow.aussiebedwars.game.upgrades;

import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.HealPoolUpgrade;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameUtils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Effect;
import org.bukkit.Location;
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
                                final Location playerEyeLoc = p.getEyeLocation().add(0.0, 0.175, 0.0);
                                activeGame.getAssociatedWorld().playEffect(playerEyeLoc, Effect.HEART, 0);
                            }
                        });
            }
        }, 1L, 20L * healPoolUpgrade.getHealFrequency());

    }

    public final void stop() {
        if (task != null) {
            task.cancel();
        }
    }
}
