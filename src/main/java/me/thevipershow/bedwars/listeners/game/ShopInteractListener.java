package me.thevipershow.bedwars.listeners.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.PotionItem;
import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.UpgradeItem;
import me.thevipershow.bedwars.config.objects.UpgradeLevel;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeType;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.Pair;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
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
        for (final ShopItem shopItem : activeGame.getBedwarsGame().getShop().getItems()) {
            if (shopItem.getSlot() == clickedSlot) {
                return shopItem;
            }
        }
        return null;
    }

    private UpgradeItem clickedUpgradeItem(final int clickedSlot) {
        for (final UpgradeItem upgradeItem : activeGame.getBedwarsGame().getShop().getUpgradeItems()) {
            if (upgradeItem.getSlot() == clickedSlot) {
                return upgradeItem;
            }
        }
        return null;
    }

    private PotionItem getClickedPotion(final int clickedSlot) {
        for (final PotionItem potionItem : activeGame.getBedwarsGame().getShop().getPotionItem()) {
            if (potionItem.getSlot() == clickedSlot) {
                return potionItem;
            }
        }
        return null;
    }

    private void performBuy(final Player player, final ItemStack clickedItem, final int clickedSlot) {
        if (!clickedAir(clickedItem)) {
            final ShopItem clickedShopItem = this.clickedShopItem(clickedSlot);
            if (clickedShopItem != null) { // clicked a ShopItem
                final Pair<HashMap<Integer, Integer>, Boolean> transaction = GameUtils.canAfford(player.getInventory(), clickedShopItem.getBuyWith(), clickedShopItem.getBuyCost());
                if (transaction.getB()) { // Give player to item
                    if (GameUtils.isArmor(clickedItem)) {
                        final String armorType = clickedItem.getType().name().split("_")[0];
                        GameUtils.makePlayerPay(player.getInventory(), clickedShopItem.getBuyWith(), clickedShopItem.getBuyCost(), transaction.getA());
                        activeGame.upgradePlayerArmorSet(player, armorType);
                        GameUtils.paySound(player);

                        final int enchantLvl = activeGame.getUpgradesLevelsMap().get(UpgradeType.REINFORCED_ARMOR).get(activeGame.getPlayerTeam(player));
                        if (enchantLvl != 0) {
                            GameUtils.enchantSwords(Enchantment.PROTECTION_ENVIRONMENTAL, enchantLvl, player); // Adding enchant if he has the Upgrade.
                        }
                    } else if (clickedItem.getType().name().endsWith("SWORD")) {
                        final ItemStack prevSword = activeGame.getSwordUpgrades().getPrevious(clickedItem.getType());
                        if (prevSword == null) {
                            GameUtils.paySound(player);
                            GameUtils.makePlayerPay(player.getInventory(), clickedShopItem.getBuyWith(), clickedShopItem.getBuyCost(), transaction.getA());
                            final ItemStack toGive = clickedShopItem.generateWithoutLore();
                            if (activeGame.getUpgradesLevelsMap().get(UpgradeType.SHARPNESS).get(activeGame.getPlayerTeam(player)) != 0) {
                                GameUtils.applyEnchant(toGive, Enchantment.DAMAGE_ALL, 1); // Adding enchant if he has the Upgrade.
                            }
                            GameUtils.giveStackToPlayer(toGive, player, player.getInventory().getContents());

                        } else { // we should always usually enter this:
                            final ItemStack search = GameUtils.hasItemOfType(player, prevSword.getType());
                            final ItemStack dupe = GameUtils.hasItemOfType(player, clickedShopItem.getMaterial());
                            final ItemStack toGive = clickedShopItem.generateWithoutLore();
                            if (dupe != null) {
                                GameUtils.buyFailSound(player);
                                player.sendMessage(Bedwars.PREFIX + "§7You have already picked this sword level.");
                                return;
                            } else if (search == null) {
                                GameUtils.giveStackToPlayer(toGive, player, player.getInventory().getContents());
                            } else {
                                GameUtils.upgradePlayerStack(player, search, toGive);
                            }
                            GameUtils.paySound(player);
                            GameUtils.makePlayerPay(player.getInventory(), clickedShopItem.getBuyWith(), clickedShopItem.getBuyCost(), transaction.getA());
                        }
                    } else {
                        GameUtils.paySound(player);
                        final ItemStack toGive = clickedShopItem.generateWithoutLore();
                        if (toGive.getType() == Material.WOOL) {
                            final BedwarsTeam pTeam = activeGame.getPlayerTeam(player);
                            toGive.setDurability(pTeam.getWoolColor());
                        } else if (toGive.getType() == Material.STAINED_GLASS) {
                            final BedwarsTeam pTeam = activeGame.getPlayerTeam(player);
                            toGive.setDurability(pTeam.getGlassColor());
                        }
                        GameUtils.makePlayerPay(player.getInventory(), clickedShopItem.getBuyWith(), clickedShopItem.getBuyCost(), transaction.getA());
                        GameUtils.giveStackToPlayer(toGive, player, player.getInventory().getContents());
                    }
                } else {
                    GameUtils.buyFailSound(player);
                    player.sendMessage(Bedwars.PREFIX + "§7You did not have enough " + GameUtils.beautifyCaps(clickedShopItem.getBuyWith().name()));
                }
            } else if (clickedItem.getType() == Material.POTION) {

                final PotionItem clickedPotion = this.getClickedPotion(clickedSlot);
                if (clickedPotion != null) {
                    final Pair<HashMap<Integer, Integer>, Boolean> transaction = GameUtils.canAfford(player.getInventory(), clickedPotion.getBuyWith(), clickedPotion.getPrice());

                    if (transaction.getB()) {
                        GameUtils.makePlayerPay(player.getInventory(), clickedPotion.getBuyWith(), clickedPotion.getPrice(), transaction.getA());
                        GameUtils.paySound(player);
                        GameUtils.giveStackToPlayer(clickedPotion.getGameStack(), player, player.getInventory().getContents());
                    } else {
                        GameUtils.buyFailSound(player);
                        player.sendMessage(Bedwars.PREFIX + "§7You did not have enough " + GameUtils.beautifyCaps(clickedPotion.getBuyWith().name()));
                    }

                }

            } else {

                final UpgradeItem clickedUpgradeItem = this.clickedUpgradeItem(clickedSlot);
                if (clickedUpgradeItem == null) {
                    return;
                } // No upgrade lvls could be found
                final List<UpgradeLevel> loadedLvls = clickedUpgradeItem.getLevels();

                final Map<UpgradeItem, Integer> playerLevels = activeGame.getPlayerUpgradeLevelsMap().get(player);
                if (playerLevels == null) return;
                final Integer currentLevel = playerLevels.get(clickedUpgradeItem);
                if (currentLevel == null) return;
                if (loadedLvls.size() <= currentLevel + 1) {
                    GameUtils.buyFailSound(player);
                    player.sendMessage(Bedwars.PREFIX + "§7You already have the highest upgrade available.");
                } else {
                    final UpgradeLevel boughtLevel = clickedUpgradeItem.getLevels().get(currentLevel + 1);
                    final Pair<HashMap<Integer, Integer>, Boolean> transaction = GameUtils.canAfford(player.getInventory(), boughtLevel.getBuyWith(), boughtLevel.getPrice());

                    if (!transaction.getB()) {
                        GameUtils.buyFailSound(player);
                        player.sendMessage(Bedwars.PREFIX + "§7You did not have enough " + GameUtils.beautifyCaps(boughtLevel.getBuyWith().name()));
                    } else {
                        GameUtils.makePlayerPay(player.getInventory(), boughtLevel.getBuyWith(), boughtLevel.getPrice(), transaction.getA());
                        final Inventory inv = activeGame.getAssociatedShopGUI().get(player.getUniqueId());
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
                        GameUtils.upgradeSound(player);
                        player.sendMessage(Bedwars.PREFIX + "§7You successfully upgraded this item to §eLvl. " + (currentLevel + 2));
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!activeGame.isHasStarted()) {
            return;
        }
        final HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }
        final Player player = (Player) entity;
        if (!player.getWorld().equals(activeGame.getAssociatedWorld())) {
            return;
        }

        final Inventory ui = activeGame.getAssociatedShopGUI().get(player.getUniqueId());
        if (ui == null || ui.getType() == InventoryType.MERCHANT) {
            return;
        }

        if (event.getClickedInventory() == null) {
            return;
        }

        if (ui.equals(event.getView().getTopInventory())) {
            event.setCancelled(true);
            final int clickedSlot = event.getSlot();
            final ItemStack clickedItem = event.getCurrentItem();
            performBuy(player, clickedItem, clickedSlot);
        }
    }
}
