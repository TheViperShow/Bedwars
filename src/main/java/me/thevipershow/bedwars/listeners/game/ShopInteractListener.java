package me.thevipershow.bedwars.listeners.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.config.objects.PotionItem;
import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.UpgradeItem;
import me.thevipershow.bedwars.config.objects.UpgradeLevel;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeType;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.Pair;
import me.thevipershow.bedwars.game.shop.ShopCategory;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class ShopInteractListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public ShopInteractListener(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private boolean clickedAir(final ItemStack clicked) {
        return clicked == null;
    }

    private ShopItem clickedShopItem(final int clickedSlot, final ShopCategory category) {
        for (final ShopItem shopItem : activeGame.getBedwarsGame().getShop().getItems()) {
            if (shopItem.getShopCategory() == category && shopItem.getSlot() == clickedSlot) {
                return shopItem;
            }
        }
        return null;
    }

    private UpgradeItem clickedUpgradeItem(final int clickedSlot, final ShopCategory category) {
        for (final UpgradeItem upgradeItem : activeGame.getBedwarsGame().getShop().getUpgradeItems()) {
            if (upgradeItem.getShopCategory() == category && upgradeItem.getSlot() == clickedSlot) {
                return upgradeItem;
            }
        }
        return null;
    }

    private PotionItem getClickedPotion(final int clickedSlot, final ShopCategory category) {
        for (final PotionItem potionItem : activeGame.getBedwarsGame().getShop().getPotionItem()) {
            if (potionItem.getShopCategory() == category && potionItem.getSlot() == clickedSlot) {
                return potionItem;
            }
        }
        return null;
    }

    private void buyShopItem(final ShopItem shopItem, final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final Pair<HashMap<Integer, Integer>, Boolean> transaction = GameUtils.canAfford(playerInventory, shopItem.getBuyWith(), shopItem.getBuyCost());

        if (transaction.getB()) {
        //    GameUtils.makePlayerPay(playerInventory, shopItem.getBuyWith(), shopItem.getBuyCost(), transaction.getA());
            if (GameUtils.isArmor(shopItem.getCachedFancyStack())) {
                final String armorType = shopItem.getCachedFancyStack().getType().name().split("_")[0];
                activeGame.upgradePlayerArmorSet(player, armorType);
                final int enchantLvl = activeGame.getUpgradesLevelsMap().get(UpgradeType.REINFORCED_ARMOR).get(activeGame.getPlayerTeam(player));
                if (enchantLvl != 0) {
                    GameUtils.enchantArmor(Enchantment.PROTECTION_ENVIRONMENTAL, enchantLvl, player); // Adding enchant if he has the Upgrade.
                }
            } else {
                GameUtils.giveStackToPlayer(shopItem.getCachedGameStack(), player, playerInventory.getContents());
            }

            GameUtils.paySound(player);
            GameUtils.makePlayerPay(playerInventory, shopItem.getBuyWith(), shopItem.getBuyCost(), transaction.getA());
        } else {
            GameUtils.buyFailSound(player);
            player.sendMessage(Bedwars.PREFIX + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(shopItem.getBuyWith().name()));
        }

    }

    private void buyPotionItem(final PotionItem potionItem, final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final Pair<HashMap<Integer, Integer>, Boolean> transaction = GameUtils.canAfford(playerInventory, potionItem.getBuyWith(), potionItem.getPrice());

        if (transaction.getB()) {
            GameUtils.giveStackToPlayer(potionItem.getGameStack(), player, playerInventory.getContents());

            GameUtils.paySound(player);
            GameUtils.makePlayerPay(playerInventory, potionItem.getBuyWith(), potionItem.getPrice(), transaction.getA());
        } else {
            GameUtils.buyFailSound(player);
            player.sendMessage(Bedwars.PREFIX + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(potionItem.getBuyWith().name()));
        }
    }

    private void buyUpgrade(final UpgradeItem upgradeItem, final Player player) {
        final List<UpgradeLevel> loadedLvls = upgradeItem.getLevels();

        final Map<UpgradeItem, Integer> playerLevels = activeGame.getPlayerUpgradeLevelsMap().get(player);
        if (playerLevels == null) {
            return;
        }
        final Integer currentLevel = playerLevels.get(upgradeItem);
        if (loadedLvls.size() <= currentLevel + 1) {
            player.sendMessage(Bedwars.PREFIX + AllStrings.YOU_ALREADY_HAVE_HIGHEST_UPGRADE_AVAILABLE.get());
            GameUtils.buyFailSound(player);
        } else {
            final UpgradeLevel boughtLevel = upgradeItem.getLevels().get(currentLevel + 1);
            final Pair<HashMap<Integer, Integer>, Boolean> transaction = GameUtils.canAfford(player.getInventory(), boughtLevel.getBuyWith(), boughtLevel.getPrice());

            if (!transaction.getB()) {
                player.sendMessage(Bedwars.PREFIX + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(boughtLevel.getBuyWith().name()));
                GameUtils.buyFailSound(player);
            } else {
                GameUtils.makePlayerPay(player.getInventory(), boughtLevel.getBuyWith(), boughtLevel.getPrice(), transaction.getA());
              //  final Inventory inv = activeGame.getAssociatedShopGUI().get(player.getUniqueId());
                final Inventory inv = activeGame.getPlayerShop().get(player.getUniqueId()).get(upgradeItem.getShopCategory());
                if (inv == null) {
                    return;
                }
                final ItemStack currentBoughtItem = boughtLevel.getCachedGameStack();
                if (loadedLvls.size() > currentLevel + 2) {
                    final UpgradeLevel toSetInGui = loadedLvls.get(currentLevel + 2);
                    inv.setItem(upgradeItem.getSlot(), toSetInGui.getCachedFancyStack());
                }
                if (currentLevel == -1) {
                    GameUtils.giveStackToPlayer(currentBoughtItem, player, player.getInventory().getContents());
                } else {
                    GameUtils.upgradePlayerStack(player, loadedLvls.get(currentLevel).getCachedGameStack(), currentBoughtItem);
                }
                player.updateInventory();
                playerLevels.computeIfPresent(upgradeItem, (k, v) -> v = v + 1);
                GameUtils.upgradeSound(player);
                player.sendMessage(Bedwars.PREFIX + AllStrings.SUCCESSFULLY_UPGRADED_TO_LVL.get() + (currentLevel + 2));
            }
        }
    }

    private void performShopClick(final Player player, final ItemStack clickedItem, final int clickedSlot, final ShopCategory shopCategory) {
        if (activeGame.getBedwarsGame().getShop().getGlassSlots().stream().noneMatch(i -> i == clickedSlot)) { // Checking if the player clicked a glass slot
                                                                                                               // doing nothing if he did.
            for (final ShopCategory category : ShopCategory.values()) { // Checking if player clicked a menu item
                if (clickedSlot == category.getSlot()) {
                    player.openInventory(activeGame.getPlayerShop().get(player.getUniqueId()).get(category)); // opening menu if he did.
                    player.playSound(player.getLocation(), Sound.NOTE_STICKS, 8.0f, 0.750f);
                    return;
                }
            }

            // player clicked a shop item:
            final Inventory clickedInv = activeGame.getPlayerShop().get(player.getUniqueId()).get(shopCategory);
            final ShopItem clickedShopItem = clickedShopItem(clickedSlot, shopCategory); // trying shopItem first:
            if (clickedShopItem != null) {
                buyShopItem(clickedShopItem, player);
            } else {
                final PotionItem potionItem = getClickedPotion(clickedSlot, shopCategory); // trying potionItem then:
                if (potionItem != null) {
                    buyPotionItem(potionItem, player);
                } else {
                    final UpgradeItem upgradeItem = clickedUpgradeItem(clickedSlot, shopCategory);
                    if (upgradeItem != null) { // It must be an upgrade item
                        buyUpgrade(upgradeItem, player);
                    }
                }
            }

        }
        /* if (!clickedAir(clickedItem)) {
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
                    } else if (clickedItem.getType().name().endsWith(AllStrings.SWORD.get())) {
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
                                player.sendMessage(Bedwars.PREFIX + AllStrings.ALREADY_SWORD_LEVEL.get());
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
                    player.sendMessage(Bedwars.PREFIX + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(clickedShopItem.getBuyWith().name()));
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
                        player.sendMessage(Bedwars.PREFIX + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(clickedPotion.getBuyWith().name()));
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
                    player.sendMessage(Bedwars.PREFIX + AllStrings.YOU_ALREADY_HAVE_HIGHEST_UPGRADE_AVAILABLE.get());
                } else {
                    final UpgradeLevel boughtLevel = clickedUpgradeItem.getLevels().get(currentLevel + 1);
                    final Pair<HashMap<Integer, Integer>, Boolean> transaction = GameUtils.canAfford(player.getInventory(), boughtLevel.getBuyWith(), boughtLevel.getPrice());

                    if (!transaction.getB()) {
                        GameUtils.buyFailSound(player);
                        player.sendMessage(Bedwars.PREFIX + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(boughtLevel.getBuyWith().name()));
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
                        player.sendMessage(Bedwars.PREFIX + AllStrings.SUCCESSFULLY_UPGRADED_TO_LVL.get() + (currentLevel + 2));
                    }
                }
            }
        }
         */
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

        for (final ShopCategory shopCategory : ShopCategory.values()) {
            if (event.getView().getTitle().equals(shopCategory.getTitle())) {
                event.setCancelled(true);
                final ItemStack clickedStack = event.getCurrentItem();
                if (clickedStack != null) {
                    this.performShopClick(player, clickedStack, event.getSlot(), shopCategory);
                }
                return;
            }
        }
    }
}
