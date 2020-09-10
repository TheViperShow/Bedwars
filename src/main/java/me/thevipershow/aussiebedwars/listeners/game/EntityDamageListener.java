package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class EntityDamageListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public EntityDamageListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity damaged = event.getEntity();
        final Entity damager = event.getDamager();
        if (!damaged.getWorld().equals(activeGame.getAssociatedWorld())) return;

        if (damaged instanceof Villager) {
            final Villager damagedVillager = (Villager) damaged;
            if (activeGame.isMerchantVillager(damagedVillager))
                event.setCancelled(true);
        } else if (activeGame.getBedwarsGame().getGamemode() != Gamemode.SOLO
                && damaged instanceof Player
                && damager instanceof Player) {
            final Player damagedPlayer = (Player) damaged;
            final Player damagerPlayer = (Player) damager;
            final BedwarsTeam damagedPlayerTeam = activeGame.getPlayerTeam(damagedPlayer);
            final BedwarsTeam damagerPlayerTeam = activeGame.getPlayerTeam(damagerPlayer);
            if (damagedPlayerTeam != null && (damagedPlayerTeam == damagerPlayerTeam))
                event.setCancelled(true);
        }
    }
}
