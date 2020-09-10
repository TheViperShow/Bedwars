package me.thevipershow.aussiebedwars.listeners.game;

import java.util.Map;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.shops.MerchantType;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import me.thevipershow.aussiebedwars.game.AbstractActiveMerchant;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class GUIInteractListener extends UnregisterableListener {
    private final ActiveGame activeGame;

    public GUIInteractListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private AbstractActiveMerchant merchantOfShop(final Inventory inventory) {
        for (final AbstractActiveMerchant activeMerchant : activeGame.getActiveMerchants())
            if (activeMerchant.getCachedInventory().equals(inventory))
                return activeMerchant;
        return null;
    }

    private boolean makePlayerBuy(final ShopItem shopItem, final Player player) {
        final Material buyWith = shopItem.getBuyWith();
        final int toPay = shopItem.getBuyCost();

        final Inventory playerInv = player.getInventory();
        final ItemStack[] contents = playerInv.getContents();
        int canPay = 0;
        int leftToPay = toPay;
        final int[][] takeFrom = new int[contents.length][1];

        for (int i = 0; i < contents.length; i++) {
            if (leftToPay < 1) break;
            final ItemStack content = contents[i];
            final Material contentMaterial = content.getType();
            if (contentMaterial != buyWith) continue;
            final int contentAmount = content.getAmount();
            if (contentAmount < toPay) {
                final int paidContent = contentAmount - canPay;
                takeFrom[i] = new int[]{paidContent};
                canPay += paidContent;
                leftToPay -= paidContent;
            } else {
                final ItemStack cloned = content.clone();
                cloned.setAmount(contentAmount - toPay);
                playerInv.setItem(i, cloned);
                return true;
            }
        }

        if (leftToPay < 1) {

            for (int i = 0; i < takeFrom.length; i++) {
                final int[] arr = takeFrom[i];
                final int toRemove = arr[0];
                if (toRemove == 0) continue;
                // final ItemStack cloned = playerInv.getItem(i).clone();
                if (toRemove == 64 || toRemove == contents[i].getAmount()) {
                    playerInv.setItem(i, null);
                } else {
                    final ItemStack cloned = contents[i].clone();
                    cloned.setAmount(cloned.getAmount() - arr[0]);
                    playerInv.setItem(i, cloned);
                }
            }
            return true;
        } else {
            player.sendMessage(AussieBedwars.PREFIX + String.format("§eYou need §6%d §emore %s to buy this!", leftToPay, shopItem.getMaterial().name()));
            return false;
        }
    }

    private ShopItem findAssociatedShopItem(final ItemStack clickedItem, final AbstractActiveMerchant abstractActiveMerchant) {
        for (final AbstractActiveMerchant activeMerchant : activeGame.getActiveMerchants()) {
            if (!abstractActiveMerchant.equals(activeMerchant)) continue;
            final Map<ItemStack, ShopItem> shopItemMap = activeMerchant.getCachedShopItems();
            for (final Map.Entry<ItemStack, ShopItem> entry : shopItemMap.entrySet())
                if (entry.getKey().equals(clickedItem))
                    return entry.getValue();
        }
        return null;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(final InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;
        final HumanEntity humanEntity = event.getWhoClicked();
        if (!humanEntity.getWorld().equals(activeGame.getAssociatedWorld())) return;
        if (!(humanEntity instanceof Player)) return;
        final AbstractActiveMerchant abstractActiveMerchant = merchantOfShop(inventory);
        if (abstractActiveMerchant == null) return;

        event.setCancelled(true);

        final ShopItem clickedItem = findAssociatedShopItem(event.getCurrentItem(), abstractActiveMerchant);

        if (clickedItem == null) return;

        switch (abstractActiveMerchant.getMerchant().getMerchantType()) {
            case SHOP: {
                makePlayerBuy(clickedItem, (Player) humanEntity);
            }
            break;
            case UPGRADE: {

            }
            break;
        }
    }
}
