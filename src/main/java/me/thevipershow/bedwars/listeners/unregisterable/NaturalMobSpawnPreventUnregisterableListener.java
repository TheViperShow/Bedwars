package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

public final class NaturalMobSpawnPreventUnregisterableListener extends UnregisterableListener {

    public NaturalMobSpawnPreventUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity toSpawn = event.getEntity();
        if (!toSpawn.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            return;
        }

        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            event.setCancelled(true);
        }
    }
}
