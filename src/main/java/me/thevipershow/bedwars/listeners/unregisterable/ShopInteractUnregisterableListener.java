package me.thevipershow.bedwars.listeners.unregisterable;

import java.util.Locale;
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
import me.thevipershow.bedwars.game.ArmorSet;
import me.thevipershow.bedwars.game.managers.GameInventoriesManager;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import me.thevipershow.bedwars.game.shop.ShopCategory;
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

    /**
     * This method is used to find out if the player has clicked on a "shop category" button.
     * If the player has selected a shop category button, he will be prompted with its associated
     * inventory.
     * @param category The ShopCategory currently open.
     * @param clickedSlot The clicked slot number.
     * @param bedwarsPlayer The BedwarsPlayer who clicked.
     * @return True if he clicked on a shop category button, false otherwise.
     */
    private boolean moveShopCategory(ShopCategory category, int clickedSlot, BedwarsPlayer bedwarsPlayer) {
        if (category.getSlot() == clickedSlot) {
            final Map<ShopCategory, Inventory> map = activeGame.getGameInventoriesManager().getPlayerShop().get(bedwarsPlayer.getUniqueId());
            if (map != null) {
                Inventory tI = map.get(category);
                if (tI != null) {
                    bedwarsPlayer.playSound(Sound.NOTE_STICKS, 9.0f, 0.650f);
                    bedwarsPlayer.getPlayer().openInventory(tI);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method is used to determine if the player clicked on a ShopItem.
     * If he clicked, the buying procedure for that item will start.
     * @param item The current ShopItem.
     * @param shopCategory The current ShopCategory.
     * @param clickedSlot The clicked slot number.
     * @param inv The Inventory currently open.
     * @param bedwarsPlayer The BedwarsPlayer who clicked.
     * @param clickedItem The Item currently clicked.
     * @return True if he clicked on a ShopItem, false otherwise.
     */
    private boolean buyShopItem(ShopItem item, ShopCategory shopCategory, int clickedSlot, Inventory inv, BedwarsPlayer bedwarsPlayer, ItemStack clickedItem) {
        if (item.getShopCategory() == shopCategory && item.getSlot() == clickedSlot) {
            Material buyWith = item.getBuyWith();
            int cost = item.getBuyCost();
            if (inv.contains(buyWith, cost)) {
                GameUtils.paySound(bedwarsPlayer.getPlayer());
                GameUtils.payMaterial(buyWith, cost, inv);

                if (GameUtils.isArmor(clickedItem)) {
                    TeamData<?> data = activeGame.getTeamManager().dataOfBedwarsPlayer(bedwarsPlayer);
                    final String type = clickedItem.getType().name().split("_")[0].toLowerCase(Locale.ROOT);
                    ArmorSet.setArmorFromType(bedwarsPlayer.getPlayer(), type, true, data.getArmorProtection());
                } else {
                    GameUtils.giveStackToPlayer(GameUtils.applyEventualColors(item.getCachedGameStack(), bedwarsPlayer), bedwarsPlayer.getPlayer(), inv.getContents());
                }
                return true;
            } else {
                GameUtils.buyFailSound(bedwarsPlayer.getPlayer());
                bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(buyWith.name()));
            }
        }
        return false;
    }

    /**
     * This method is used to determine if the player clicked on a PotionItem.
     * If he clicked, the buying procedure for that item will start.
     * @param item The current PotionItem.
     * @param shopCategory The current ShopCategory.
     * @param clickedSlot The clicked slot number.
     * @param inv The Inventory currently open.
     * @param bedwarsPlayer The BedwarsPlayer who clicked.
     * @return True if he clicked on a ShopItem, false otherwise.
     */
    private boolean buyPotionItem(PotionItem item, ShopCategory shopCategory, int clickedSlot, Inventory inv, BedwarsPlayer bedwarsPlayer) {
        if (item.getShopCategory() == shopCategory && item.getSlot() == clickedSlot) {
            Material buyWith = item.getBuyWith();
            int cost = item.getPrice();
            if (inv.contains(buyWith, cost)) {
                GameUtils.paySound(bedwarsPlayer.getPlayer());
                GameUtils.payMaterial(buyWith, cost, inv);
                GameUtils.giveStackToPlayer(item.getGameStack(), bedwarsPlayer.getPlayer(), inv.getContents());
                return true;
            } else {
                GameUtils.buyFailSound(bedwarsPlayer.getPlayer());
                bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(buyWith.name()));
            }
        }
        return false;

    }

    /**
     * This method is used to determine if the player clicked on a UpgradeItem.
     * If he clicked, the upgrading procedure for that item will start.
     * @param upgradeItem The current UpgradeItem.
     * @param shopCategory The current ShopCategory.
     * @param clickedSlot The clicked slot number.
     * @param inv The Inventory currently open.
     * @param bedwarsPlayer The BedwarsPlayer who clicked.
     * @return True if the player clicked on a UpgradeItem, false otherwise.
     */
    private boolean buyUpgradeItem(UpgradeItem upgradeItem, ShopCategory shopCategory, int clickedSlot, Inventory inv, BedwarsPlayer bedwarsPlayer) {
        if (upgradeItem.getShopCategory() != shopCategory || upgradeItem.getSlot() != clickedSlot) {
            return false;
        }
        GameInventoriesManager gameInventoriesManager = activeGame.getGameInventoriesManager();
        if (gameInventoriesManager.canUpgrade(bedwarsPlayer, upgradeItem)) {
            UpgradeLevel next = gameInventoriesManager.getNextUpgradeLevel(bedwarsPlayer, upgradeItem);

            Material buyWith = next.getBuyWith();
            int cost = next.getPrice();

            if (inv.contains(buyWith, cost)) {
                gameInventoriesManager.updateItemUpgrade(shopCategory, upgradeItem, clickedSlot, bedwarsPlayer.getUniqueId());
                GameUtils.payMaterial(buyWith, cost, bedwarsPlayer.getInventory());
                GameUtils.paySound(bedwarsPlayer.getPlayer());
                GameUtils.giveStackToPlayer(next.generateGameStack(), bedwarsPlayer.getPlayer(), bedwarsPlayer.getInventory().getContents());
                return true;
            } else {
                bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(buyWith.name()));
                GameUtils.buyFailSound(bedwarsPlayer.getPlayer());
            }
        } else {
            bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.YOU_ALREADY_HAVE_HIGHEST_UPGRADE_AVAILABLE.get());
            GameUtils.buyFailSound(bedwarsPlayer.getPlayer());
        }
        return false;
    }

    /**
     * Handles all of the logic for how to proceed with a purchase.
     * @param bedwarsPlayer The BedwarsPlayer who wants to purchase something.
     * @param shopCategory The ShopCategory he's currently looking at.
     * @param clickedSlot The number of the clicked slot.
     * @param clickedItem The clicked item.
     */
    private void shopLogic(BedwarsPlayer bedwarsPlayer, ShopCategory shopCategory, int clickedSlot, ItemStack clickedItem) {
        BedwarsGame bedwarsGame = activeGame.getBedwarsGame();
        Shop shop = bedwarsGame.getShop();
        if (shop.getGlassSlots().stream().anyMatch(i -> clickedSlot == i)) {
            return;
        }
        Inventory inv = bedwarsPlayer.getInventory();

        for (ShopCategory category : ShopCategory.values()) {
            if (moveShopCategory(category, clickedSlot, bedwarsPlayer)) return;
        }

        for (ShopItem item : shop.getItems()) {
            if (buyShopItem(item, shopCategory, clickedSlot, inv, bedwarsPlayer, clickedItem)) return;
        }

        for (PotionItem item : shop.getPotionItem()) {
            if (buyPotionItem(item, shopCategory, clickedSlot, inv, bedwarsPlayer)) return;
        }

        for (UpgradeItem upgradeItem : shop.getUpgradeItems()) {
            if (buyUpgradeItem(upgradeItem, shopCategory, clickedSlot, inv, bedwarsPlayer)) return;
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

        Inventory topInventory = event.getInventory();

        ShopCategory open = activeGame.getGameInventoriesManager().getOpenShopCategory(uuid, topInventory);

        if (open != null && bedwarsPlayer != null) {
            int clickedSlot = event.getSlot();
            ItemStack clickedStack = event.getCurrentItem();
            if (clickedStack != null) {
                shopLogic(bedwarsPlayer, open, clickedSlot, clickedStack);
                event.setCancelled(true);
            }
        }
    }
}
