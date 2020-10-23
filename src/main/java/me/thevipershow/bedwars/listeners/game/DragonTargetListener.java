package me.thevipershow.bedwars.listeners.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public final class DragonTargetListener extends UnregisterableListener {

    private final Map<CraftEnderDragon, BedwarsTeam> dragonPlayerMap = new HashMap<>();

    private final ActiveGame activeGame;

    public DragonTargetListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true,priority = EventPriority.HIGHEST)
    public void onEntityTargetLivingEntity(final EntityTargetLivingEntityEvent event) {
        if (!event.getEntity().getWorld().equals(activeGame.getAssociatedWorld())) {
            return;
        }
        if (!(event.getEntity() instanceof EnderDragon)) {
            return;
        }
        if (!(event.getTarget() instanceof Player)) {
            return;
        }
        if (!activeGame.isHasStarted()) {
            return;
        }
        final Player target = (Player) event.getTarget();
        final EnderDragon enderDragon = (EnderDragon) event.getEntity();
        final CraftEnderDragon craftEnderDragon = (CraftEnderDragon) enderDragon;
        if (dragonPlayerMap.containsKey(craftEnderDragon)) {
            final BedwarsTeam dragonOwnerTeam = dragonPlayerMap.get(craftEnderDragon);
            if (activeGame.getPlayerTeam(target) == dragonOwnerTeam) {
                event.setCancelled(true);

                final Optional<Player> newTarget = activeGame.getAssignedTeams()
                        .entrySet()
                        .stream()
                        .filter(k -> k.getKey() != dragonOwnerTeam)
                        .flatMap(v -> v.getValue().stream())
                        .filter(p -> activeGame.getPlayersOutOfGame().contains(p))
                        .findAny();

                newTarget.ifPresent(p -> craftEnderDragon.getHandle().setGoalTarget(((CraftPlayer) p).getHandle()));
            }
        }
    }

    public final Map<CraftEnderDragon, BedwarsTeam> getDragonPlayerMap() {
        return dragonPlayerMap;
    }
}
