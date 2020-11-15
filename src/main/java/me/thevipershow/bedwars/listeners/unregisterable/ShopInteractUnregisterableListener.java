package me.thevipershow.bedwars.listeners.unregisterable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import me.thevipershow.bedwars.config.objects.PotionItem;
import me.thevipershow.bedwars.config.objects.Shop;
import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.UpgradeItem;
import me.thevipershow.bedwars.config.objects.UpgradeLevel;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.objects.BedwarsPlayer;
import me.thevipershow.bedwars.game.shop.ShopCategory;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ShopInteractUnregisterableListener extends UnregisterableListener {
    public ShopInteractUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    private void shopLogic(BedwarsPlayer bedwarsPlayer, ShopCategory shopCategory, int clickedSlot, ItemStack clickedItem) {
        final BedwarsGame bedwarsGame = activeGame.getBedwarsGame();
        final Shop shop = bedwarsGame.getShop();
        if (shop.getGlassSlots().stream().anyMatch(i -> clickedSlot == i)) {
            return;
        }

        final Inventory inv = bedwarsPlayer.getInventory();

        for (final ShopCategory category : ShopCategory.values()) {
            if (category.getSlot() == clickedSlot) {
                final Map<ShopCategory, Inventory> map = activeGame.getGameInventories().getInventories();
                if (map != null) {
                    final Inventory tI = map.get(category);
                    if (tI != null) {
                        bedwarsPlayer.getPlayer().openInventory(tI);
                        bedwarsPlayer.playSound(Sound.NOTE_STICKS, 9.50f, 0.805f);
                    }
                }
                return;
            }
        }

        for (final ShopItem item : shop.getItems()) {
            if (item.getShopCategory() == shopCategory && item.getSlot() == clickedSlot) {
                Material buyWith = item.getBuyWith();
                int cost = item.getBuyCost();
                if (inv.contains(buyWith, cost)) {
                    GameUtils.paySound(bedwarsPlayer.getPlayer());
                    GameUtils.payMaterial(buyWith, cost, inv);
                    GameUtils.giveStackToPlayer(item.getCachedGameStack(), bedwarsPlayer.getPlayer(), inv.getContents());
                    //inv.addItem(item.getCachedGameStack());
                } else {
                    GameUtils.buyFailSound(bedwarsPlayer.getPlayer());
                    bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(buyWith.name()));
                }
                return;
            }
        }

        for (final PotionItem item : shop.getPotionItem()) {
            if (item.getShopCategory() == shopCategory && item.getSlot() == clickedSlot) {
                Material buyWith = item.getBuyWith();
                int cost = item.getPrice();
                if (inv.contains(buyWith, cost)) {
                    GameUtils.paySound(bedwarsPlayer.getPlayer());
                    GameUtils.payMaterial(buyWith, cost, inv);
                    GameUtils.giveStackToPlayer(item.getGameStack(), bedwarsPlayer.getPlayer(), inv.getContents());
                } else {
                    GameUtils.buyFailSound(bedwarsPlayer.getPlayer());
                    bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(buyWith.name()));
                }
                return;
            }
        }

        for (final UpgradeItem item : shop.getUpgradeItems()) {
            if (item.getShopCategory() == shopCategory && item.getSlot() == clickedSlot) {
                final List<UpgradeLevel> levels = item.getLevels();
                int currentLevel = bedwarsPlayer.getUpgradeItemLevel(item);
                UpgradeLevel upgradeLevel = levels.get(currentLevel);
                Material buyWith = upgradeLevel.getBuyWith();
                int cost = upgradeLevel.getPrice();
                if (inv.contains(buyWith, cost)) {
                    if(bedwarsPlayer.upgradeAndGiveItem(item)) {
                        GameUtils.paySound(bedwarsPlayer.getPlayer());
                        GameUtils.payMaterial(buyWith, cost, inv);
                        if (currentLevel + 1 != levels.size()) {
                            activeGame.getGameInventories().updateItemUpgrade(shopCategory, clickedSlot, levels.get(currentLevel + 1), bedwarsPlayer.getUniqueId());
                        }
                    } else {
                        GameUtils.buyFailSound(bedwarsPlayer.getPlayer());
                        bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.ALREADY_BOUGHT_MAX_LVL.get());
                    }
                } else {
                    GameUtils.buyFailSound(bedwarsPlayer.getPlayer());
                    bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(buyWith.name()));
                }

                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public final void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        UUID uuid = humanEntity.getUniqueId();
        if (!humanEntity.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            return;
        }

        BedwarsPlayer bedwarsPlayer = activeGame.getPlayerMapper().get(uuid);

        Inventory topInventory = event.getView().getTopInventory();

        ShopCategory open = activeGame.getGameInventories().getOpenShopCategory(uuid, topInventory);

        if (open != null && bedwarsPlayer != null) {
            int clickedSlot = event.getSlot();
            ItemStack clickedStack = event.getCurrentItem();
            if (clickedStack != null) {
                event.setCancelled(true);
                shopLogic(bedwarsPlayer, open, clickedSlot, clickedStack);
            }
        }
    }
}
