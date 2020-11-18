package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public final class HungerLossListener extends UnregisterableListener {

    public HungerLossListener(ActiveGame activeGame) {
        super(activeGame);
    }

    @EventHandler(ignoreCancelled = true,priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!event.getEntity().getWorld().equals(activeGame.getCachedGameData().getGame())) {
            return;
        }

        event.setCancelled(true);
    }


}
