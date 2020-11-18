package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import net.minecraft.server.v1_8_R3.EntityLargeFireball;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class FireballShootUnregisterableListener extends UnregisterableListener {
    public FireballShootUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    static final class CustomFireball extends EntityLargeFireball {

        public CustomFireball(World world, EntityLiving entityliving, double d0, double d1, double d2) {
            super(world, entityliving, d0, d1, d2);
        }

        @Override
        public final void setDirection(double d0, double d1, double d2) {
            this.dirX = d0;
            this.dirY = d1;
            this.dirZ = d2;
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!player.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            return;
        }

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
            final Location newLocation = pEyeLoc.clone().add(pEyeLoc.getDirection().multiply(1.750));
            final Vector fireballDirection = pEyeLoc.getDirection();

            final CustomFireball fireball = new CustomFireball(
                    ((CraftWorld) player.getWorld()).getHandle(),
                    ((CraftPlayer) player).getHandle(),
                    fireballDirection.getX() / 4.8250, fireballDirection.getY() / 4.8250, fireballDirection.getZ() / 4.8250);

            fireball.locX = newLocation.getX();
            fireball.locY = newLocation.getY();
            fireball.locZ = newLocation.getZ();

            final CraftWorld craftWorld = (CraftWorld) pEyeLoc.getWorld();

            craftWorld.addEntity(fireball, CreatureSpawnEvent.SpawnReason.CUSTOM);

            event.setCancelled(true);
            GameUtils.decreaseItemInHand(player);
        }
    }
}
