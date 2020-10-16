package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class PlayerFireballInteractListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public PlayerFireballInteractListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!player.getWorld().equals(activeGame.getAssociatedWorld())) return;

        final Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            final ItemStack itemInHand = player.getInventory().getItemInHand();

            if (itemInHand == null) {
                return;
            }
            if (itemInHand.getType() != Material.FIREBALL) {
                return;
            }

            final Location pEyeLoc = player.getEyeLocation();
            final Vector playerDirection = pEyeLoc.getDirection();
            final Location shootLocation = pEyeLoc.add(playerDirection.clone().multiply(1.050));

            final Fireball fireball = (Fireball) activeGame.getAssociatedWorld().spawnEntity(shootLocation, EntityType.FIREBALL);
            fireball.setVelocity(shootLocation.getDirection());
            fireball.setIsIncendiary(true);
            event.setCancelled(true);
            GameUtils.decreaseItemInHand(player);
        }
    }

}
