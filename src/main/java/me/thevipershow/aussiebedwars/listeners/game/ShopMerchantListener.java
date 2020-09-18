package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.bedwars.objects.shops.MerchantType;
import me.thevipershow.aussiebedwars.game.AbstractActiveMerchant;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public final class ShopMerchantListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public ShopMerchantListener(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        final Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager)) return;
        final World w = entity.getWorld();
        if (!w.equals(activeGame.getAssociatedWorld())) return;
        final Player p = event.getPlayer();
        final Villager villager = (Villager) entity;

        AbstractActiveMerchant aMerchant = null;

        for (final AbstractActiveMerchant activeMerchant : activeGame.getActiveMerchants()) {
            final Villager activeMerchantVillager = activeMerchant.getVillager();
            if (activeMerchantVillager == villager && activeMerchant.getMerchant().getMerchantType() == MerchantType.SHOP) {
                aMerchant = activeMerchant;
                break;
            }
        }

        if (aMerchant == null) return;

        event.setCancelled(true);
        activeGame.openShop(p);
    }
}
