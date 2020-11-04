package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class ChestInteractListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public ChestInteractListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!player.getWorld().equals(activeGame.getLobbyWorld())) {
            return;
        }
        if (!activeGame.isHasStarted()) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null && clickedBlock.getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
                activeGame.getEnderchestManager().openEnderchest(player);
            }
        }
    }
}
