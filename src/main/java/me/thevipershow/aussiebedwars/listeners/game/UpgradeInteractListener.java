package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class UpgradeInteractListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public UpgradeInteractListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(final InventoryClickEvent event) {
        final HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) return;
        final Player player = (Player) entity;
        if (!player.getWorld().equals(activeGame.getAssociatedWorld())) return;

        final Inventory ui = activeGame.getAssociatedUpgradeGUI().get(player);
        if (ui == null) return;

        if (event.getClickedInventory() == null) return;
        if (ui.equals(event.getView().getTopInventory())) {
            event.setCancelled(true);
            final int clickedSlot = event.getSlot();
            final ItemStack clickedItem = event.getCurrentItem();
        }
    }
}
