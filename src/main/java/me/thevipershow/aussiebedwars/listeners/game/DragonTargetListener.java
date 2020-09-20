package me.thevipershow.aussiebedwars.listeners.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public final class DragonTargetListener extends UnregisterableListener {

    private final Map<CraftEnderDragon, List<Player>> dragonPlayerMap = new HashMap<>();

    private final ActiveGame activeGame;

    public DragonTargetListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityTargetLivingEntity(final EntityTargetLivingEntityEvent event) {
        if (!event.getEntity().getWorld().equals(activeGame.getAssociatedWorld())) return;
        if (!(event.getEntity() instanceof EnderDragon)) return;
        final EnderDragon enderDragon = (EnderDragon) event.getEntity();
        final CraftEnderDragon craftEnderDragon = (CraftEnderDragon) enderDragon;
        if (dragonPlayerMap.containsKey(craftEnderDragon)) {
            final List<Player> dragonOwner = dragonPlayerMap.get(craftEnderDragon);
            if (event.getTarget().equals(dragonOwner)) {
                event.setCancelled(true);
                final Optional<Player> newTarget = activeGame.getAssociatedQueue().getInQueue()
                        .stream().filter(p -> !activeGame.getPlayersOutOfGame().contains(p) && dragonOwner.stream().noneMatch(o -> o.equals(p)))
                        .findAny();

                newTarget.ifPresent(p -> craftEnderDragon.getHandle().setGoalTarget(((CraftPlayer) p).getHandle()));
            }
        }
    }

    public final Map<CraftEnderDragon, List<Player>> getDragonPlayerMap() {
        return dragonPlayerMap;
    }
}
