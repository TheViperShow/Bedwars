package me.thevipershow.aussiebedwars.listeners.game;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        if (clickedAir(clickedItem)) {
            return;
        } else {
            final ShopItem clickedShopItem = clickedShopItem(clickedSlot);
            if (clickedShopItem != null) { // clicked a ShopItem
                final Pair<HashMap<Integer, Integer>, Boolean> transaction = GameUtils.canAfford(player.getInventory(), clickedShopItem.getBuyWith(), clickedShopItem.getBuyCost());
                if (transaction.getB()) { // Give player to item
                    GameUtils.makePlayerPay(player.getInventory(), clickedShopItem.getBuyWith(), clickedShopItem.getBuyCost(), transaction.getA());
                    GameUtils.giveStackToPlayer(clickedShopItem.generateWithoutLore(), player, player.getInventory().getContents());
                } else {
                    player.sendMessage(AussieBedwars.PREFIX + "§7You did not have enough " + GameUtils.beautifyCaps(clickedShopItem.getBuyWith().name()));
                }
                return;
            }

            final UpgradeItem clickedUpgradeItem = clickedUpgradeItem(clickedSlot);
            if (clickedUpgradeItem == null) return; // No upgrade lvls could be found

            final Map<UpgradeItem, Integer> playerLevels = activeGame.getPlayerUpgradeLevelsMap().get(player);
            if (playerLevels == null) return;
            final Integer currentLevel = playerLevels.get(clickedUpgradeItem);
            if (currentLevel == null) return;
            final UpgradeLevel nextLevel = clickedUpgradeItem.getLevels().get(currentLevel + 1);
            if (nextLevel == null) {
                player.sendMessage(AussieBedwars.PREFIX + "§7This is already at its maximum level.");
            } else {
                final Pair<HashMap<Integer, Integer>, Boolean> transaction = GameUtils.canAfford(player.getInventory(), nextLevel.getBuyWith(), nextLevel.getPrice());

                if (!transaction.getB()) { // player cannot afford this upgrade
                    player.sendMessage(AussieBedwars.PREFIX + "§7You did not have enough " + GameUtils.beautifyCaps(nextLevel.getBuyWith().name()));
                } else { // player can buy this item
                    GameUtils.makePlayerPay(player.getInventory(), nextLevel.getBuyWith(), nextLevel.getPrice(), transaction.getA());
                    final Inventory inv = activeGame.getAssociatedGui().get(player);
                    final ItemStack nextLvlItem = Objects.requireNonNull(nextLevel.getCachedFancyStack());
                    inv.setItem(clickedSlot, Objects.requireNonNull(nextLvlItem));
                    if (currentLevel == 0) {
                        GameUtils.giveStackToPlayer(nextLvlItem, player, player.getInventory().getContents());
                    } else {
                        GameUtils.upgradePlayerStack(player, clickedUpgradeItem.getLevels().get(currentLevel).getCachedGameStack(), nextLvlItem);
                    }
                    player.updateInventory();
                    playerLevels.computeIfPresent(clickedUpgradeItem, (k, v) -> v++);
                }
            }
        }

/*
        final List<UpgradeLevel> clickedLevel;
        clickedLevel = clickedUpgradeItem.getLevels().stream()
                .sorted(Comparator.comparing(UpgradeLevel::getLevel))
                .collect(Collectors.toList());
        if (clickedLevel.isEmpty()) return; // No upgrade items could be found
        // otherwise found one.
        final ListIterator<UpgradeLevel> levelIterator = clickedLevel.listIterator();
        while (levelIterator.hasNext()) {

            final UpgradeLevel current = levelIterator.next();
            if (current.getCachedFancyStack().isSimilar(clickedItem)) { // found the clicked level

                // making player buy it
                final Pair<HashMap<Integer, Integer>, Boolean> transaction = GameUtils.canAfford(player.getInventory(), current.getBuyWith(), current.getPrice());

                if (!transaction.getB()) { // player cannot afford this upgrade
                    player.sendMessage(AussieBedwars.PREFIX + "§7You did not have enough " + GameUtils.beautifyCaps(current.getBuyWith().name()));
                    return;
                } else { // player can buy this item

                    final boolean hasNext = levelIterator.hasNext();
                    if (!hasNext) { // Item doesn' have any upgrades available
                        player.sendMessage(AussieBedwars.PREFIX + "§7This is already at its maximum level.");
                    } else { // Player can and will get his item upgraded
                        GameUtils.makePlayerPay(player.getInventory(), current.getBuyWith(), current.getPrice(), transaction.getA());
                        GameUtils.giveStackToPlayer(current.generateGameStack(), player, player.getInventory().getContents());
                        final Inventory inv = activeGame.getAssociatedGui().get(player);
                        final UpgradeLevel nextLvl = levelIterator.next();
                        final ItemStack nextLvlItem = Objects.requireNonNull(nextLvl.getCachedFancyStack());
                        inv.setItem(clickedSlot, Objects.requireNonNull(nextLvlItem));
                        player.updateInventory();
                        return;
                    }
                }
            }
        }
    }

 */

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
