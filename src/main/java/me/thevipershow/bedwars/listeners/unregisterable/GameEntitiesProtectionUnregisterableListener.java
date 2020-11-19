package me.thevipershow.bedwars.listeners.unregisterable;

import java.util.Map;
import java.util.UUID;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.api.BedwarsFriendlyFireEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

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
                .filter(s -> !s.getSpawner().isInvisible())
                .anyMatch(s -> s.getStand().getUniqueId().equals(uuid));
    }

    private BedwarsPlayer isFriend(BedwarsTeam attackerTeam, UUID attacked) {
        for (Map.Entry<BedwarsTeam, ? extends TeamData<?>> entry : activeGame.getTeamManager().getDataMap().entrySet()) {
            if (entry.getKey() == attackerTeam) {
                for (BedwarsPlayer bedwarsPlayer : entry.getValue().getAll()) {
                    if (bedwarsPlayer.getUniqueId().equals(attacked)) {
                        return bedwarsPlayer;
                    }
                }
                break;
            }
        }

        return null;
    }

    @EventHandler(ignoreCancelled = true)
    public final void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        UUID damagedUUID = damaged.getUniqueId();
        if (!activeGame.getCachedGameData().getGame().equals(damaged.getWorld())) {
            return;
        }

        if (isGameMerchant(damagedUUID) || isArmorStand(damagedUUID)) {
            event.setCancelled(true);
        }

        Entity damager = event.getDamager();
        if (damager.getType() == EntityType.PLAYER) {
            BedwarsPlayer damagerBedwarsPlayer = activeGame.getPlayerMapper().get((Player) damager);
            if (damagerBedwarsPlayer == null) {
                return;
            }
            BedwarsPlayer friend = isFriend(damagerBedwarsPlayer.getBedwarsTeam(), damagedUUID);
            if (friend != null) {
                BedwarsFriendlyFireEvent friendlyFireEvent = new BedwarsFriendlyFireEvent(activeGame, damagerBedwarsPlayer, friend);
                if (!friendlyFireEvent.isCancelled()) {
                    event.setCancelled(true);
                }
            }

        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!entity.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            return;
        }
        if (entity.getType() == EntityType.ARMOR_STAND) {
            event.setCancelled(true);
        }
    }
}
