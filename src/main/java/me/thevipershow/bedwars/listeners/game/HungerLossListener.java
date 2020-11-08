package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;

@Deprecated
public class HungerLossListener extends UnregisterableListener {

    public HungerLossListener(ActiveGame activeGame) {
        super(activeGame);
    }

    /*
    @EventHandler(ignoreCancelled = true,priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        if (!activeGame.isHasStarted()) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!event.getEntity().getWorld().equals(activeGame.getAssociatedWorld())) {
            return;
        }
        event.setCancelled(true);
    }

     */
}
