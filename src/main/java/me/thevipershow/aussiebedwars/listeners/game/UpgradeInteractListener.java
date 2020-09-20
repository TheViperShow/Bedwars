package me.thevipershow.aussiebedwars.listeners.game;

import java.util.Map;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.DragonBuffUpgrade;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.HealPoolUpgrade;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.IronForgeUpgrade;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.ManiacMinerUpgrade;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.ReinforcedArmorUpgrade;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.SharpnessUpgrade;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.UpgradeShop;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameUtils;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class UpgradeInteractListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public UpgradeInteractListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private static void maxLevel(final Player player) {
        GameUtils.buyFailSound(player);
        player.sendMessage(AussieBedwars.PREFIX + "You already have bought maximum level");
    }

    public final void upgradeLogic(final Player player, final int clickedSlot, final ItemStack clickedItem) {
        final BedwarsTeam pTeam = activeGame.getPlayerTeam(player);

        final UpgradeShop upgradeShop = activeGame.getBedwarsGame().getUpgradeShop();
        final DragonBuffUpgrade dragonBuffUpgrade = upgradeShop.getDragonBuffUpgrade();
        final HealPoolUpgrade healPoolUpgrade = upgradeShop.getHealPoolUpgrade();
        final IronForgeUpgrade ironForgeUpgrade = upgradeShop.getIronForgeUpgrade();
        final ManiacMinerUpgrade maniacMinerUpgrade = upgradeShop.getManiacMinerUpgrade();
        final ReinforcedArmorUpgrade reinforcedArmorUpgrade = upgradeShop.getReinforcedArmorUpgrade();
        final SharpnessUpgrade sharpnessUpgrade = upgradeShop.getSharpnessUpgrade();
        if (clickedSlot == dragonBuffUpgrade.getShopItem().getSlot()) {
            final int currentLevel = activeGame.getUpgradesLevelsMap().get(dragonBuffUpgrade.getType()).get(pTeam);
            if (currentLevel == 1) {
                maxLevel(player);
            } else {
                // TODO: Add dragon buff logic
                // here
                activeGame.getUpgradesLevelsMap().get(dragonBuffUpgrade.getType()).put(pTeam, 1);
            }
        } else if (clickedSlot == healPoolUpgrade.getItem().getSlot()) {
            final int currentLevel = activeGame.getUpgradesLevelsMap().get(healPoolUpgrade.getType()).get(pTeam);
            if (currentLevel == 1) {
                maxLevel(player);
            } else {
                // TODO: Add Heal pool logic
                // here
                activeGame.getUpgradesLevelsMap().get(healPoolUpgrade.getType()).put(pTeam, 1);
            }
        } else if (clickedSlot == ironForgeUpgrade.getSlot()) {
            final int currentLevel = activeGame.getUpgradesLevelsMap().get(ironForgeUpgrade.getType()).get(pTeam);
            final int maxLevel = ironForgeUpgrade.getLevels().size();
            if (currentLevel == maxLevel) {
                maxLevel(player);
            } else {
                // TODO: Add Iron Forge logic
                // here
                activeGame.getUpgradesLevelsMap().get(ironForgeUpgrade.getType()).put(pTeam, maxLevel);
            }
        } else if (clickedSlot == maniacMinerUpgrade.getSlot()) {
            final int currentLevel = activeGame.getUpgradesLevelsMap().get(maniacMinerUpgrade.getType()).get(pTeam);
            final int maxLevel = maniacMinerUpgrade.getLevels().size();
            if (currentLevel == maxLevel) {
                maxLevel(player);
            } else {
                // TODO: Add Maniac Miner logic
                // here
                activeGame.getUpgradesLevelsMap().get(maniacMinerUpgrade.getType()).put(pTeam, maxLevel);
            }
        } else if (clickedSlot == reinforcedArmorUpgrade.getSlot()) {
            final int currentLevel = activeGame.getUpgradesLevelsMap().get(reinforcedArmorUpgrade.getType()).get(pTeam);
            final int maxLevel = reinforcedArmorUpgrade.getLevels().size();
            if (currentLevel == maxLevel) {
                maxLevel(player);
            } else {
                // TODO: Add Reinforced Armor logic
                // here
                activeGame.getUpgradesLevelsMap().get(reinforcedArmorUpgrade.getType()).put(pTeam, maxLevel);
            }
        } else if (clickedSlot == sharpnessUpgrade.getItem().getSlot()) {
            final int currentLevel = activeGame.getUpgradesLevelsMap().get(sharpnessUpgrade.getType()).get(pTeam);
            if (currentLevel == 1) {
                maxLevel(player);
            } else {
                // TODO: Add dragon buff logic
                // here
                activeGame.getUpgradesLevelsMap().get(sharpnessUpgrade.getType()).put(pTeam, 1);
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
