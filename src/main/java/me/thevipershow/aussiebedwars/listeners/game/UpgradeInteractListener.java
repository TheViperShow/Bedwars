package me.thevipershow.aussiebedwars.listeners.game;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.DragonBuffUpgrade;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.HealPoolUpgrade;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.IronForgeUpgrade;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.ManiacMinerUpgrade;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.ReinforcedArmorUpgrade;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.SharpnessUpgrade;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.UpgradeShop;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.UpgradeShopItem;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.UpgradeType;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameUtils;
import me.thevipershow.aussiebedwars.game.Pair;
import me.thevipershow.aussiebedwars.game.upgrades.ActiveHealPool;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class UpgradeInteractListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public UpgradeInteractListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private static void maxLevel(final Player player) {
        GameUtils.buyFailSound(player);
        player.sendMessage(AussieBedwars.PREFIX + "You already have bought maximum level");
    }

    private static boolean pay(final Pair<HashMap<Integer, Integer>, Boolean> result, final Player player, final Material payWith, final int cost, final String upgradeName) {
        if (!result.getB()) {
            player.sendMessage(AussieBedwars.PREFIX + "You cannot afford this upgrade.");
            GameUtils.buyFailSound(player);
            return false;
        } else {
            GameUtils.makePlayerPay(player.getInventory(), payWith, cost, result.getA());
            GameUtils.paySound(player);
            player.sendMessage(AussieBedwars.PREFIX + "You successfully upgraded: §e" + upgradeName );
            return true;
        }
    }

    public final void upgradeLogic(final Player player, final int clickedSlot, final ItemStack clickedItem) {
        final BedwarsTeam pTeam = activeGame.getPlayerTeam(player);
        final PlayerInventory playerInventory = player.getInventory();

        final UpgradeShop upgradeShop = activeGame.getBedwarsGame().getUpgradeShop();
        final DragonBuffUpgrade dragonBuffUpgrade = upgradeShop.getDragonBuffUpgrade();
        final HealPoolUpgrade healPoolUpgrade = upgradeShop.getHealPoolUpgrade();
        final IronForgeUpgrade ironForgeUpgrade = upgradeShop.getIronForgeUpgrade();
        final ManiacMinerUpgrade maniacMinerUpgrade = upgradeShop.getManiacMinerUpgrade();
        final ReinforcedArmorUpgrade reinforcedArmorUpgrade = upgradeShop.getReinforcedArmorUpgrade();
        final SharpnessUpgrade sharpnessUpgrade = upgradeShop.getSharpnessUpgrade();

        final EnumMap<UpgradeType, Map<BedwarsTeam, Integer>> upgradesAvailable = activeGame.getUpgradesLevelsMap();

        if (clickedSlot == dragonBuffUpgrade.getShopItem().getSlot()) {
            final int currentLevel = upgradesAvailable.get(dragonBuffUpgrade.getType()).get(pTeam);
            if (currentLevel == 1) {
                maxLevel(player);
            } else {
                final ShopItem shopItem = dragonBuffUpgrade.getShopItem();
                Pair<HashMap<Integer, Integer>, Boolean> pay = GameUtils.canAfford(playerInventory, shopItem.getBuyWith(), shopItem.getBuyCost());
                if(pay(pay, player, shopItem.getBuyWith(), shopItem.getBuyCost(), "Dragon Buff")) {
                    upgradesAvailable.get(dragonBuffUpgrade.getType()).put(pTeam, 1);
                }
                // Dragon Buff ^
            }
        } else if (clickedSlot == healPoolUpgrade.getItem().getSlot()) {
            final Map<BedwarsTeam, Integer> healPoolTeamLevelsMap = Objects.requireNonNull(upgradesAvailable.get(healPoolUpgrade.getType()));
            final int currentLevel = Objects.requireNonNull(healPoolTeamLevelsMap.get(pTeam));
            if (currentLevel == 1) {
                maxLevel(player);
            } else {
                final ShopItem shopItem = healPoolUpgrade.getItem();
                Pair<HashMap<Integer, Integer>, Boolean> pay = GameUtils.canAfford(playerInventory, shopItem.getBuyWith(), shopItem.getBuyCost());
                if(pay(pay, player, shopItem.getBuyWith(), shopItem.getBuyCost(), "Heal Pool")) {
                    upgradesAvailable.get(healPoolUpgrade.getType()).put(pTeam, 1);
                    final ActiveHealPool activeHealPool = new ActiveHealPool(activeGame, pTeam, healPoolUpgrade);
                    activeGame.getHealPools().add(activeHealPool);
                    activeHealPool.start();

                    upgradesAvailable.get(healPoolUpgrade.getType()).put(pTeam, 1);
                }
                // Heal Pool ^
            }
        } else if (clickedSlot == ironForgeUpgrade.getSlot()) {
            final int currentLevel = upgradesAvailable.get(ironForgeUpgrade.getType()).get(pTeam);
            final int maxLevel = ironForgeUpgrade.getLevels().size();
            if (currentLevel == maxLevel) {
                maxLevel(player);
            } else {
                final UpgradeShopItem itemToBuy = ironForgeUpgrade.getLevels().get(currentLevel);
                // TODO: Add Iron Forge logic
                // here
                final Pair<HashMap<Integer, Integer>, Boolean> pay = GameUtils.canAfford(playerInventory, itemToBuy.getBuyWith(), itemToBuy.getPrice());
                if (pay(pay, player, itemToBuy.getBuyWith(), itemToBuy.getPrice(), "Iron Forge")) {
                    upgradesAvailable.get(ironForgeUpgrade.getType()).put(pTeam, currentLevel + 1);
                    activeGame.getTeamSpawners(pTeam).forEach(s -> s.setDropSpeedRegulator((currentLevel + 1) * 50));
                }
                upgradesAvailable.get(ironForgeUpgrade.getType()).put(pTeam, currentLevel + 1);
            }
        } else if (clickedSlot == maniacMinerUpgrade.getSlot()) {
            final int currentLevel = upgradesAvailable.get(maniacMinerUpgrade.getType()).get(pTeam);
            final int maxLevel = maniacMinerUpgrade.getLevels().size();
            if (currentLevel == maxLevel) {
                maxLevel(player);
            } else {
                // TODO: Add Maniac Miner logic
                // here
                upgradesAvailable.get(maniacMinerUpgrade.getType()).put(pTeam, maxLevel);
            }
        } else if (clickedSlot == reinforcedArmorUpgrade.getSlot()) {
            final int currentLevel = upgradesAvailable.get(reinforcedArmorUpgrade.getType()).get(pTeam);
            final int maxLevel = reinforcedArmorUpgrade.getLevels().size();
            if (currentLevel == maxLevel) {
                maxLevel(player);
            } else {
                // TODO: Add Reinforced Armor logic
                // here
                upgradesAvailable.get(reinforcedArmorUpgrade.getType()).put(pTeam, maxLevel);
            }
        } else if (clickedSlot == sharpnessUpgrade.getItem().getSlot()) {
            final int currentLevel = upgradesAvailable.get(sharpnessUpgrade.getType()).get(pTeam);
            if (currentLevel == 1) {
                maxLevel(player);
            } else {
                // TODO: Add dragon buff logic
                // here
                upgradesAvailable.get(sharpnessUpgrade.getType()).put(pTeam, 1);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(final InventoryClickEvent event) {
        final HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) return;
        final Player player = (Player) entity;
        if (!player.getWorld().equals(activeGame.getAssociatedWorld())) return;

        final Inventory ui = activeGame.getAssociatedUpgradeGUI().get(player);
        if (ui == null) return;

        if (event.getClickedInventory() == null) return;

        if (ui.equals(event.getView().getTopInventory())) {
            event.setCancelled(true);
            final int clickedSlot = event.getSlot();
            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;
            upgradeLogic(player, clickedSlot, clickedItem);
        }
    }
}
