package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveGameState;
import me.thevipershow.bedwars.game.upgrades.merchants.impl.ShopActiveMerchant;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public final class ShopMerchantUnregisterableListener extends UnregisterableListener {
    public ShopMerchantUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        final Entity entity = event.getRightClicked();
        if (activeGame.getGameState() != ActiveGameState.STARTED) {
            return;
        }
        if (!(entity instanceof Villager)) {
            return;
        }
        final World w = entity.getWorld();
        if (!w.equals(activeGame.getCachedGameData().getGame())) {
            return;
        }
        final Player p = event.getPlayer();
        final Villager villager = (Villager) entity;
        final ShopActiveMerchant shopActiveMerchant = activeGame.getMerchantManager().getShopActiveMerchant(villager);
        if (shopActiveMerchant != null) {
            event.setCancelled(true);
            activeGame.getGameInventories().openShop(p);
        }
    }
}
