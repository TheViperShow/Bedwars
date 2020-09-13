package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class PlayerFireballInteractListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public PlayerFireballInteractListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!player.getWorld().equals(activeGame.getAssociatedWorld())) return;

        final ItemStack itemInHand = player.getItemInHand();
        if (itemInHand == null) return;
        if (itemInHand.getType() != Material.FIREBALL) return;

        player.setItemInHand(null);
        final Vector playerDirection = player.getEyeLocation().getDirection();
        final Fireball fireball = (Fireball) activeGame.getAssociatedWorld().spawnEntity(player.getEyeLocation(), EntityType.FIREBALL);
        fireball.setDirection(playerDirection);
        fireball.setIsIncendiary(false);
    }

}
