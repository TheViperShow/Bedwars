package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ShopActiveMerchant;
import me.thevipershow.bedwars.game.shop.ShopCategory;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public final class ShopMerchantListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public ShopMerchantListener(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        if (!activeGame.isHasStarted()) {
            return;
        }
        final Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager)) {
            return;
        }
        final World w = entity.getWorld();
        if (!w.equals(activeGame.getAssociatedWorld())) {
            return;
        }

        final Player p = event.getPlayer();

        final Villager villager = (Villager) entity;
        final ShopActiveMerchant shopActiveMerchant = activeGame.getTeamShopActiveMerchant(villager);

        if (shopActiveMerchant != null) {
            p.openInventory(activeGame.getPlayerShop().get(p.getUniqueId()).get(ShopCategory.QUICK_BUY));
            event.setCancelled(true);
        }
    }
}
