package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public final class LobbyCompassListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public LobbyCompassListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(final PlayerInteractEvent event) {
        final Player clicker = event.getPlayer();

        if (!clicker.getWorld().equals(activeGame.getAssociatedWorld())) return;

        final ItemStack clickedItem = clicker.getItemInHand();
        if (clickedItem != null && clickedItem.getType() == Material.COMPASS) {
            clicker.getInventory().clear();
            clicker.teleport(activeGame.getCachedLobbySpawnLocation());
            clicker.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }
}
