package me.thevipershow.bedwars.listeners.unregisterable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.upgradeshop.StagedUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.TrapUpgrades;
import me.thevipershow.bedwars.config.objects.upgradeshop.Upgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShop;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShopItem;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeType;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.AlarmTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.BlindnessAndPoisonTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.CounterOffensiveTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.MinerFatigueTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.ShopUpgrade;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveGameState;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.data.Pair;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import me.thevipershow.bedwars.game.managers.UpgradesManager;
import me.thevipershow.bedwars.game.upgrades.traps.ActiveTrap;
import me.thevipershow.bedwars.game.upgrades.traps.AlarmActiveTrap;
import me.thevipershow.bedwars.game.upgrades.traps.BlindnessPoisonActiveTrap;
import me.thevipershow.bedwars.game.upgrades.traps.CounterOffensiveActiveTrap;
import me.thevipershow.bedwars.game.upgrades.traps.MinerFatigueActiveTrap;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class UpgradeInteractUnregisterableListener extends UnregisterableListener {
    public UpgradeInteractUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    private void addTrap(Player player, Class<? extends ActiveTrap> activeTrapClass, BedwarsTeam playerTeam, int slot, ShopItem item) {
        try {
            activeGame.getTrapsManager()
                    .getActiveTraps()
                    .get(playerTeam)
                    .addLast(activeTrapClass.getConstructor(BedwarsTeam.class, ActiveGame.class).newInstance(playerTeam, activeGame));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        activeGame.getGameInventories().getAssociatedTrapsGUI().get(player.getUniqueId()).setItem(slot, item.getCachedFancyStack());
        activeGame.getGameInventories().getAssociatedUpgradeGUI().get(player.getUniqueId()).setItem(slot, item.getCachedFancyStack());
    }

    private static boolean payTraps(Pair<HashMap<Integer, Integer>, Boolean> result, Player player, ShopItem buying, String boughtName) {
        if (!result.getB()) {
            player.sendMessage(Bedwars.PREFIX + AllStrings.CANNOT_AFFORD_TRAP.get());
            GameUtils.buyFailSound(player);
            return false;
        } else {
            GameUtils.makePlayerPay(player.getInventory(), buying.getBuyWith(), buying.getBuyCost(), result.getA());
            GameUtils.paySound(player);
            player.sendMessage(Bedwars.PREFIX + AllStrings.SUCCESSFULLY_ENABLED_TRAP.get() + boughtName);
            return true;
        }
    }

    private void trapLogic(BedwarsPlayer bedwarsPlayer, int clickedSlot) {
        Player player = bedwarsPlayer.getPlayer();
        UpgradeShop upgradeShop = activeGame.getBedwarsGame().getUpgradeShop();
        TrapUpgrades trapUpgrades = upgradeShop.getTrapUpgrades();

        AlarmTrap alarmTrap = trapUpgrades.getAlarmTrap();
        BlindnessAndPoisonTrap blindnessAndPoisonTrap = trapUpgrades.getBlindnessAndPoisonTrap();
        CounterOffensiveTrap counterOffensiveTrap = trapUpgrades.getCounterOffensiveTrap();
        MinerFatigueTrap minerFatigueTrap = trapUpgrades.getMinerFatigueTrap();

        ShopItem alarmShopItem = alarmTrap.getShopItem();
        ShopItem blindnessAndPoisonShopItem = blindnessAndPoisonTrap.getShopItem();
        ShopItem counterOffensiveShopItem = counterOffensiveTrap.getShopItem();
        ShopItem minerFatigueShopItem = minerFatigueTrap.getShopItem();
        BedwarsTeam playerTeam = bedwarsPlayer.getBedwarsTeam();
        LinkedList<ActiveTrap> activeTeamTraps = activeGame.getTrapsManager().getActiveTraps().get(playerTeam);
        int slotToSet = 30 + activeTeamTraps.size();
        if (activeTeamTraps.size() >= 3) {
            player.sendMessage(Bedwars.PREFIX + AllStrings.MAX_TRAPS_LIMIT.get());
            player.playSound(player.getLocation(), Sound.ARROW_HIT, 10.0f, 0.85f);
        } else if (alarmShopItem.getSlot() == clickedSlot) {
            Pair<HashMap<Integer, Integer>, Boolean> payment = GameUtils.canAfford(player.getInventory(), alarmShopItem.getBuyWith(), alarmShopItem.getBuyCost());
            if (payTraps(payment, player, alarmShopItem, AllStrings.ALARM_TRAP_DISPLAY.get())) {
                addTrap(player, AlarmActiveTrap.class, playerTeam, slotToSet, alarmShopItem);
            }
        } else if (blindnessAndPoisonShopItem.getSlot() == clickedSlot) {
            Pair<HashMap<Integer, Integer>, Boolean> payment = GameUtils.canAfford(player.getInventory(), blindnessAndPoisonShopItem.getBuyWith(), blindnessAndPoisonShopItem.getBuyCost());
            if (payTraps(payment, player, blindnessAndPoisonShopItem, AllStrings.BP_TRAP_DISPLAY.get())) {
                addTrap(player, BlindnessPoisonActiveTrap.class, playerTeam, slotToSet, blindnessAndPoisonShopItem);
            }
        } else if (counterOffensiveShopItem.getSlot() == clickedSlot) {
            Pair<HashMap<Integer, Integer>, Boolean> payment = GameUtils.canAfford(player.getInventory(), counterOffensiveShopItem.getBuyWith(), counterOffensiveShopItem.getBuyCost());
            if (payTraps(payment, player, counterOffensiveShopItem, AllStrings.CO_TRAP_DISPLAY.get())) {
                addTrap(player, CounterOffensiveActiveTrap.class, playerTeam, slotToSet, counterOffensiveShopItem);
            }
        } else if (minerFatigueShopItem.getSlot() == clickedSlot) {
            Pair<HashMap<Integer, Integer>, Boolean> payment = GameUtils.canAfford(player.getInventory(), minerFatigueShopItem.getBuyWith(), minerFatigueShopItem.getBuyCost());
            if (payTraps(payment, player, minerFatigueShopItem, AllStrings.MF_TRAP_DISPLAY.get())) {
                addTrap(player, MinerFatigueActiveTrap.class, playerTeam, slotToSet, minerFatigueShopItem);
            }
        }
    }

    private void upgradeLogic(BedwarsPlayer bedwarsPlayer, int clickedSlot) {
        Player player = bedwarsPlayer.getPlayer();
        PlayerInventory playerInventory = player.getInventory();

        UpgradeShop upgradeShop = activeGame.getBedwarsGame().getUpgradeShop();

        UpgradesManager upgradesManager = activeGame.getUpgradesManager();

        TrapUpgrades trapUpgrades = upgradeShop.getTrapUpgrades();
        if (clickedSlot != trapUpgrades.getSlot()) {
            for (UpgradeType upgradeType : UpgradeType.values()) {
                Upgrade upgrade = upgradeShop.getUpgrade(upgradeType);
                int upgradeSlot = upgrade.getSlot();
                if (clickedSlot == upgradeSlot) {
                    TeamData<?> teamData = activeGame.getTeamManager().dataOfBedwarsPlayer(bedwarsPlayer);
                    boolean hasStages = upgrade.hasStages();
                    if (hasStages) {
                        StagedUpgrade stagedUpgrade = (StagedUpgrade) upgrade;
                        int currentLevel = teamData.getUpgradeLevel(upgradeType);
                        if (currentLevel + 1 == stagedUpgrade.getLevels().size()) {
                            GameUtils.buyFailSound(player);
                            player.sendMessage(Bedwars.PREFIX + AllStrings.ALREADY_BOUGHT_MAX_LVL.get());
                        } else {
                            UpgradeShopItem currentItem = stagedUpgrade.getLevels().get(currentLevel + 1);
                            if (GameUtils.pay(player, playerInventory, currentItem.getBuyWith(), currentItem.getPrice(),
                                    Bedwars.PREFIX + AllStrings.SUCCESSFULLY_UPGRADED_TO_LVL.get() + (currentLevel + 1),
                                    Bedwars.PREFIX + AllStrings.CANNOT_AFFORD_UPGRADE.get())) {
                                teamData.increaseLevel(upgradeType);
                                upgradesManager.upgrade(bedwarsPlayer, upgradeType, upgrade);
                            }
                        }
                    } else {
                        ShopUpgrade shopUpgrade = (ShopUpgrade) upgrade;
                        ShopItem shopItem = shopUpgrade.getShopItem();
                        if (teamData.getUpgradeLevel(upgradeType) == 0) {
                            if (GameUtils.pay(player, playerInventory, shopItem.getBuyWith(), shopItem.getBuyCost(),
                                    Bedwars.PREFIX + AllStrings.SUCCESSFULLY_UPGRADED.get(),
                                    Bedwars.PREFIX + AllStrings.CANNOT_AFFORD_UPGRADE.get())) {
                                teamData.increaseLevel(upgradeType);
                                upgradesManager.upgrade(bedwarsPlayer, upgradeType, upgrade);
                            }
                        } else {
                            GameUtils.buyFailSound(player);
                            player.sendMessage(Bedwars.PREFIX + AllStrings.ALREADY_BOUGHT_MAX_LVL.get());
                        }
                    }
                }
            }
        } else {
            activeGame.getGameInventories().openTraps(player);
        }

    }

    @EventHandler(ignoreCancelled = true)
    public final void onInventoryClick(final InventoryClickEvent event) {
        if (activeGame.getGameState() != ActiveGameState.STARTED) {
            return;
        }

        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;
        if (!player.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            return;
        }

        Inventory ui = activeGame.getGameInventories().getAssociatedUpgradeGUI().get(player.getUniqueId());
        if (ui == null) {
            return;
        }

        Inventory traps = activeGame.getGameInventories().getAssociatedTrapsGUI().get(player.getUniqueId());

        if (event.getClickedInventory() == null) {
            return;
        }

        Inventory topInventory = event.getView().getTopInventory();

        BedwarsPlayer bedwarsPlayer = activeGame.getPlayerMapper().get(player);

        int clickedSlot = event.getSlot();
        if (ui != null && ui.equals(topInventory)) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) {
                return;
            }
            upgradeLogic(bedwarsPlayer, clickedSlot);
        } else if (traps != null && traps.equals(topInventory)) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) {
                return;
            }
            trapLogic(bedwarsPlayer, clickedSlot);
        }
    }
}
