package me.thevipershow.bedwars.listeners.unregisterable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveGameState;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public final class DragonRedirectorListener extends UnregisterableListener {
    public DragonRedirectorListener(ActiveGame activeGame) {
        super(activeGame);
    }

    private final Map<UUID, BedwarsTeam> dragonTeamsMap = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (!event.getEntity().getWorld().equals(activeGame.getCachedGameData().getGame())) {
            return;
        }
        if (!(event.getEntity() instanceof EnderDragon)) {
            return;
        }
        if (!(event.getTarget() instanceof Player)) {
            return;
        }
        if (activeGame.getGameState() != ActiveGameState.STARTED) {
            return;
        }
        Player target = (Player) event.getTarget();
        EnderDragon enderDragon = (EnderDragon) event.getEntity();

        UUID dragonUUID = enderDragon.getUniqueId();
        if (dragonTeamsMap.containsKey(dragonUUID)) {
            BedwarsTeam dragonOwnerTeam = dragonTeamsMap.get(dragonUUID);
            if (activeGame.getPlayerMapper().get(target).getBedwarsTeam() == dragonOwnerTeam) {
                event.setCancelled(true);
                activeGame.getTeamManager()
                        .getDataMap()
                        .values()
                        .stream()
                        .flatMap(v -> v.getAll().stream())
                        .filter(b -> b.getBedwarsTeam() != dragonOwnerTeam)
                        .findAny()
                        .ifPresent(b -> ((Creature) enderDragon).setTarget(b.getPlayer()));

            }
        }
    }

    public final Map<UUID, BedwarsTeam> getDragonTeamsMap() {
        return dragonTeamsMap;
    }
}
