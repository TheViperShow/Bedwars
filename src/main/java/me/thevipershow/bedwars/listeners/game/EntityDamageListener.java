package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public final class EntityDamageListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public EntityDamageListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private boolean isInQueueRoom(final Entity player) {
        return activeGame.getCachedWaitingLocation().distanceSquared(player.getLocation()) <= 500;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity damaged = event.getEntity();
        final Entity damager = event.getDamager();
        if (!damaged.getWorld().equals(activeGame.getAssociatedWorld())) return;

        if (isInQueueRoom(damaged)) {
            event.setCancelled(true);
        } else if (damaged instanceof Villager) {
            final Villager damagedVillager = (Villager) damaged;
            if (activeGame.isMerchantVillager(damagedVillager)) {
                event.setCancelled(true);
            }
        } else if (damaged instanceof ArmorStand) {
            event.setCancelled(true);
        } else if (activeGame.getBedwarsGame().getGamemode() != Gamemode.SOLO
                && damaged instanceof Player
                && damager instanceof Player) {
            final Player damagedPlayer = (Player) damaged;
            final Player damagerPlayer = (Player) damager;
            final BedwarsTeam damagedPlayerTeam = activeGame.getPlayerTeam(damagedPlayer);
            final BedwarsTeam damagerPlayerTeam = activeGame.getPlayerTeam(damagerPlayer);
            if (damagedPlayerTeam != null && (damagedPlayerTeam == damagerPlayerTeam)) {
                event.setCancelled(true);
            }
        } else if (damager instanceof EnderDragon && damaged instanceof Player) {
            final Vector dragonVelocity = damager.getVelocity();
            damaged.setVelocity(new Vector(dragonVelocity.getX(), 4.0, dragonVelocity.getZ()));
        } else if (damaged instanceof Player && activeGame.isOutOfGame((Player) damaged)) {
            event.setCancelled(true);
        } else if (damaged instanceof Player && activeGame.getHiddenPlayers().contains(damaged)) {
            activeGame.showPlayer((Player) damaged);
        }
    }
}
