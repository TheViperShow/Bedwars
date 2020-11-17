package me.thevipershow.bedwars.listeners.unregisterable;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.objects.BedwarsPlayer;
import me.thevipershow.bedwars.game.objects.TeamData;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class GameEntitiesProtectionUnregisterableListener extends UnregisterableListener {

    public GameEntitiesProtectionUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    private boolean isGameMerchant(UUID uuid) {
        return activeGame.getMerchantManager().getActiveMerchants()
                .stream()
                .anyMatch(merchant -> merchant.getVillager().getUniqueId().equals(uuid));
    }

    private boolean isArmorStand(UUID uuid) {
        return activeGame.getActiveSpawnersManager().getActiveSpawners()
                .stream()
                .anyMatch(s -> s.getStand().getUniqueId().equals(uuid));
    }

    private boolean isFriend(BedwarsTeam attackerTeam, UUID attacked) {
        for (Map.Entry<BedwarsTeam, ? extends TeamData<?>> entry : activeGame.getTeamManager().getDataMap().entrySet()) {
            if (entry.getKey() == attackerTeam) {
                for (BedwarsPlayer bedwarsPlayer : entry.getValue().getAll()) {
                    if (bedwarsPlayer.getUniqueId().equals(attacked)) {
                        return true;
                    }
                }
                break;
            }
        }
        return false;
        //return activeGame.getTeamManager().getDataMap().entrySet()
        //         .stream()
        //        .filter(e -> e.getKey() == attackerTeam)
        //        .anyMatch(e -> e.getValue().getAll().stream().anyMatch(bp -> bp.getUniqueId().equals(attacked)));
    }

    @EventHandler(ignoreCancelled = true)
    public final void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        UUID damagedUUID = damaged.getUniqueId();
        if (!activeGame.getCachedGameData().getGame().equals(damaged.getWorld())) {
            return;
        }


    }
}
