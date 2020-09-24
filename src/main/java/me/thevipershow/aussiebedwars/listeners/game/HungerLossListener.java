package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class HungerLossListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public HungerLossListener(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!event.getEntity().getWorld().equals(activeGame.getAssociatedWorld())) return;
        event.setCancelled(true);
    }
}
