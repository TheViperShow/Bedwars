package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Deprecated
public final class LobbyCompassListener extends UnregisterableListener {
    public LobbyCompassListener(ActiveGame activeGame) {
        super(activeGame);
    }

    /*

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(final PlayerInteractEvent event) {
        final Player clicker = event.getPlayer();

        if (!activeGame.isHasStarted()) {
            return;
        }

        if (!activeGame.getPlayersOutOfGame().contains(clicker)) {
            return;
        }
        if (!clicker.getWorld().equals(activeGame.getAssociatedWorld())) {
            return;
        }

        final ItemStack clickedItem = clicker.getItemInHand();
        if (clickedItem != null && clickedItem.getType() == Material.COMPASS && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            clicker.getInventory().clear();
            activeGame.getAssociatedQueue().removeFromQueue(clicker);
            clicker.teleport(activeGame.getCachedLobbySpawnLocation());
            GameUtils.removeAllEffects(clicker);
            clicker.setFlying(false);
            clicker.setAllowFlight(false);
            final Scoreboard scoreboard = activeGame.getActiveScoreboards().get(clicker);
            if (scoreboard != null) {
                scoreboard.deactivate();
                activeGame.getActiveScoreboards().remove(clicker);
            }
        }
    }

     */
}
