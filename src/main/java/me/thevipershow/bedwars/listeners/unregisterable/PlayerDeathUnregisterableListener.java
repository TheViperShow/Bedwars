package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.events.BedwarsPlayerDeathEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.objects.BedwarsPlayer;
import me.thevipershow.bedwars.game.objects.TeamStatus;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public final class PlayerDeathUnregisterableListener extends UnregisterableListener {

    public PlayerDeathUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    private boolean isFinalKill(BedwarsPlayer bedwarsPlayer) {
        return activeGame.getTeamManager().dataOfBedwarsPlayer(bedwarsPlayer).getStatus() == TeamStatus.BED_BROKEN;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    final public void onEntityDamage(EntityDamageEvent event) {
        DamageCause cause = event.getCause();
        Entity damaged = event.getEntity();
        if (damaged.getType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) damaged;
        BedwarsPlayer bedwarsPlayer = activeGame.getPlayerMapper().get(player);
        boolean isInVoid = player.getLocation().getY() <= -0.00d;
        boolean isKill = player.getHealth() - event.getFinalDamage() <= +0.00d;
        boolean isFinalKill = isFinalKill(bedwarsPlayer);

        if (isKill || isInVoid) {
            final BedwarsPlayerDeathEvent bedwarsPlayerDeathEvent;
            if (!(event instanceof EntityDamageByEntityEvent)) {
                bedwarsPlayerDeathEvent = new BedwarsPlayerDeathEvent(this.activeGame, cause, bedwarsPlayer, null, null, isFinalKill);
            } else {
                Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                if (damager.getType() != EntityType.PLAYER) {
                    bedwarsPlayerDeathEvent = new BedwarsPlayerDeathEvent(this.activeGame, cause, bedwarsPlayer, damager, null, isFinalKill);
                } else {
                    BedwarsPlayer killer = activeGame.getPlayerMapper().get(damager.getUniqueId());
                    bedwarsPlayerDeathEvent = new BedwarsPlayerDeathEvent(this.activeGame, cause, bedwarsPlayer, damager, killer, isFinalKill);
                }
            }
            activeGame.getPlugin().getServer().getPluginManager().callEvent(bedwarsPlayerDeathEvent);

            if (!bedwarsPlayerDeathEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }

    }
}
