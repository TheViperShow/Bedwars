package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameUtils;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class LobbyCompassListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public LobbyCompassListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(final PlayerInteractEvent event) {
        final Player clicker = event.getPlayer();

        if (!activeGame.getPlayersOutOfGame().contains(clicker)) return;
        if (!clicker.getWorld().equals(activeGame.getAssociatedWorld())) return;

        final ItemStack clickedItem = clicker.getItemInHand();
        if (clickedItem != null && clickedItem.getType() == Material.COMPASS) {
            clicker.getInventory().clear();
            activeGame.getAssociatedQueue().removeFromQueue(clicker);
            clicker.teleport(activeGame.getCachedLobbySpawnLocation());
            GameUtils.removeAllEffects(clicker);
            clicker.setFlying(false);
            clicker.setAllowFlight(false);
        }
    }
}
