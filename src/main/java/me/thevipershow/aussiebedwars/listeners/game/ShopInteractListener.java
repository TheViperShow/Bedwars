package me.thevipershow.aussiebedwars.listeners.game;

import java.util.List;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import me.thevipershow.aussiebedwars.config.objects.UpgradeItem;
import me.thevipershow.aussiebedwars.config.objects.UpgradeLevel;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameUtils;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
            final ItemMeta clickedMeta = clickedItem.getItemMeta();
            final ShopItem clickedShopItem = clickedShopItem(clickedSlot);
            if (clickedShopItem != null) {
                final boolean transaction = GameUtils.makePlayerPay(player.getInventory(), clickedShopItem.getBuyWith(), clickedShopItem.getBuyCost());
                if (transaction) { // Give player to item
                    GameUtils.giveStackToPlayer(clickedShopItem.generateWithoutLore(), player, player.getInventory().getContents());
                } else {
                    player.sendMessage(AussieBedwars.PREFIX + "§7You did not have enough " + GameUtils.beautifyCaps(clickedShopItem.getBuyWith().name()));
                }
                return;
            }

            final UpgradeItem clickedUpgradeItem = clickedUpgradeItem(clickedSlot);
            if (clickedUpgradeItem != null) {

                final List<UpgradeLevel> lvls = clickedUpgradeItem.getLevels();
                for (int i = 0; i < lvls.size(); i++) {
                    final UpgradeLevel lvl = lvls.get(i);
                    if (lvl.getItemName().equals(clickedMeta.getDisplayName())) {
                        final UpgradeLevel nextLvl = lvls.get(i + 1);
                        if (nextLvl != null) {
                            final boolean transaction = GameUtils.makePlayerPay(player.getInventory(), nextLvl.getBuyWith(), nextLvl.getPrice());
                            if (transaction) { // Upgrade player's item.
                                final ItemStack toSet = nextLvl.generateGameStack();
                                final UpgradeLevel nextNextLevel = lvls.get(i + 2);
                                activeGame.getAssociatedGui().get(player).setItem(clickedSlot, nextNextLevel == null ? toSet : nextNextLevel.generateFancyStack());
                                player.getInventory().setItem(clickedSlot, toSet);
                            } else {
                                player.sendMessage(AussieBedwars.PREFIX + "§7You did not have enough " + GameUtils.beautifyCaps(nextLvl.getBuyWith().name()));
                            }
                        } else {
                            player.sendMessage(AussieBedwars.PREFIX + "§7This is already at maximum level.");
                        }
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        final Player player = (Player) event.getWhoClicked();
        if (player.getWorld().equals(activeGame.getAssociatedWorld())) return;
        final Inventory playerUi = activeGame.getAssociatedGui().get(player);
        if (playerUi == null) return;
        if (event.getInventory().equals(playerUi)) event.setCancelled(true);
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

        if (ui.equals(event.getClickedInventory())) {
            final int clickedSlot = event.getSlot();
            final ItemStack clickedItem = event.getCurrentItem();
            performBuy(player, clickedItem, clickedSlot);
            event.setCancelled(true);
        }
    }
}
