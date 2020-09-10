package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class LobbyCompassListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public LobbyCompassListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(final InventoryClickEvent event) {
        final HumanEntity clicker = event.getWhoClicked();
        if (!clicker.getWorld().equals(activeGame.getAssociatedWorld())) return;

        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickedItem.getType() == Material.COMPASS) {
            clicker.teleport(activeGame.getCachedLobbySpawnLocation());
        }
    }
}
