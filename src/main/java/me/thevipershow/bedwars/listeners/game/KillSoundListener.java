package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class KillSoundListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public KillSoundListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        final Entity damaged = event.getEntity();
        if (damager.getType() != EntityType.PLAYER && damaged.getType() != EntityType.PLAYER) {
            return;
        }

        final Player killer = (Player) damager;
        final Player killed = (Player) damaged;

        if (killer.getWorld().equals(activeGame.getAssociatedWorld()) && killed.getWorld().equals(activeGame.getAssociatedWorld())) {

            if (killed.getHealth() - event.getDamage() <= 0.0) {
                killed.playSound(killer.getLocation(), Sound.NOTE_PLING, 8.0f, 1.00f);
                killer.playSound(killer.getLocation(), Sound.NOTE_PLING, 8.0f, 1.00f);
            }
        }
    }
}
