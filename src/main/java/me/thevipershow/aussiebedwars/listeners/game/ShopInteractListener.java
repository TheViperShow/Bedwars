package me.thevipershow.aussiebedwars.listeners.game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import me.thevipershow.aussiebedwars.config.objects.UpgradeItem;
import me.thevipershow.aussiebedwars.config.objects.UpgradeLevel;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameUtils;
import me.thevipershow.aussiebedwars.game.Pair;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class ShopInteractListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public ShopInteractListener(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private boolean clickedAir(final ItemStack clicked) {
        return clicked == null;
    }

    private ShopItem clickedShopItem(final int clickedSlot) {
        for (final ShopItem shopItem : activeGame.getBedwarsGame().getShop().getItems())
            if (shopItem.getSlot() == clickedSlot) return shopItem;
        return null;
    }

    private UpgradeItem clickedUpgradeItem(final int clickedSlot) {
        for (final UpgradeItem upgradeItem : activeGame.getBedwarsGame().getShop().getUpgradeItems())
            if (upgradeItem.getSlot() == clickedSlot) return upgradeItem;
        return null;
    }

    private void performBuy(final Player player, final ItemStack clickedItem, final int clickedSlot) {
        if (!clickedAir(clickedItem)) {
            final ShopItem clickedShopItem = clickedShopItem(clickedSlot);
            if (clickedShopItem != null) { // clicked a ShopItem
                final Pair<HashMap<Integer, Integer>, Boolean> transaction = GameUtils.canAfford(player.getInventory(), clickedShopItem.getBuyWith(), clickedShopItem.getBuyCost());
                if (transaction.getB()) { // Give player to item
                    if (GameUtils.isArmor(clickedItem)) {
                        final String armorType = clickedItem.getType().name().split("_")[0];
                        if (!armorType.equals("DIAMOND")) {
                            GameUtils.makePlayerPay(player.getInventory(), clickedShopItem.getBuyWith(), clickedShopItem.getBuyCost(), transaction.getA());
                            activeGame.upgradePlayerArmorSet(player, armorType);
                        } else {
                            player.sendMessage(AussieBedwars.PREFIX + "§7You already have the highest upgrade available.");
                        }
                    } else if (clickedItem.getType().name().endsWith("SWORD")) {
                        final ItemStack prevSword = activeGame.getSwordUpgrades().getPrevious(clickedItem.getType());
                        if (prevSword == null) {
                            GameUtils.makePlayerPay(player.getInventory(), clickedShopItem.getBuyWith(), clickedShopItem.getBuyCost(), transaction.getA());
                            GameUtils.giveStackToPlayer(clickedShopItem.generateWithoutLore(), player, player.getInventory().getContents());
                        } else { // we should always usually enter this:
                            final ItemStack search = GameUtils.hasItemOfType(player, prevSword.getType());
                            final ItemStack dupe = GameUtils.hasItemOfType(player, clickedShopItem.getMaterial());
                            final ItemStack toGive = clickedShopItem.generateWithoutLore();
                            if (dupe != null) {
                                player.sendMessage(AussieBedwars.PREFIX + "§7You have already picked this sword level.");
                                return;
                            } else if (search == null) {
                                GameUtils.giveStackToPlayer(toGive, player, player.getInventory().getContents());
                            } else {
                                GameUtils.upgradePlayerStack(player, search, toGive);
                            }
                            GameUtils.makePlayerPay(player.getInventory(), clickedShopItem.getBuyWith(), clickedShopItem.getBuyCost(), transaction.getA());
                        }
                    } else {
                        GameUtils.makePlayerPay(player.getInventory(), clickedShopItem.getBuyWith(), clickedShopItem.getBuyCost(), transaction.getA());
                        GameUtils.giveStackToPlayer(clickedShopItem.generateWithoutLore(), player, player.getInventory().getContents());
                    }
                } else {
                    player.sendMessage(AussieBedwars.PREFIX + "§7You did not have enough " + GameUtils.beautifyCaps(clickedShopItem.getBuyWith().name()));
                }
                return;
            }

            final UpgradeItem clickedUpgradeItem = clickedUpgradeItem(clickedSlot);
            if (clickedUpgradeItem == null) return; // No upgrade lvls could be found
            final List<UpgradeLevel> loadedLvls = clickedUpgradeItem.getLevels();

            final Map<UpgradeItem, Integer> playerLevels = activeGame.getPlayerUpgradeLevelsMap().get(player);
            if (playerLevels == null) return;
            final Integer currentLevel = playerLevels.get(clickedUpgradeItem);
            if (currentLevel == null) return;
            if (loadedLvls.size() <= currentLevel + 1) {
                player.sendMessage(AussieBedwars.PREFIX + "§7You already have the highest upgrade available.");
            } else {
                final UpgradeLevel boughtLevel = clickedUpgradeItem.getLevels().get(currentLevel + 1);
                final Pair<HashMap<Integer, Integer>, Boolean> transaction = GameUtils.canAfford(player.getInventory(), boughtLevel.getBuyWith(), boughtLevel.getPrice());

                if (!transaction.getB()) {
                    player.sendMessage(AussieBedwars.PREFIX + "§7You did not have enough " + GameUtils.beautifyCaps(boughtLevel.getBuyWith().name()));
                } else {
                    GameUtils.makePlayerPay(player.getInventory(), boughtLevel.getBuyWith(), boughtLevel.getPrice(), transaction.getA());
                    final Inventory inv = activeGame.getAssociatedGui().get(player);
                    final ItemStack currentBoughtItem = boughtLevel.getCachedGameStack();
                    if (loadedLvls.size() > currentLevel + 2) {
                        final UpgradeLevel toSetInGui = loadedLvls.get(currentLevel + 2);
                        inv.setItem(clickedSlot, toSetInGui.getCachedFancyStack());
                    }
                    if (currentLevel == -1) {
                        GameUtils.giveStackToPlayer(currentBoughtItem, player, player.getInventory().getContents());
                    } else {
                        GameUtils.upgradePlayerStack(player, loadedLvls.get(currentLevel).getCachedGameStack(), currentBoughtItem);
                    }
                    player.updateInventory();
                    playerLevels.computeIfPresent(clickedUpgradeItem, (k, v) -> v = v + 1);
                    player.sendMessage(AussieBedwars.PREFIX + "§7You successfully upgraded this item to §eLvl. " + (currentLevel + 2));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        final HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) return;
        final Player player = (Player) entity;
        if (!player.getWorld().equals(activeGame.getAssociatedWorld())) return;

        final Inventory ui = activeGame.getAssociatedGui().get(player);
        if (ui == null) return;

        if (event.getClickedInventory() == null) return;

        if (ui.equals(event.getView().getTopInventory())) {
            event.setCancelled(true);
            final int clickedSlot = event.getSlot();
            final ItemStack clickedItem = event.getCurrentItem();
            performBuy(player, clickedItem, clickedSlot);
        }
    }
}
