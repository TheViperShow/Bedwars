package me.thevipershow.bedwars.listeners.unregisterable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.thevipershow.bedwars.AllStrings;
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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

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
        activeGame.getGameInventoriesManager().getAssociatedTrapsGUI().get(player.getUniqueId()).setItem(slot, item.getCachedFancyStack());
        activeGame.getGameInventoriesManager().getAssociatedUpgradeGUI().get(player.getUniqueId()).setItem(slot, item.getCachedFancyStack());
    }

    private static boolean payTraps(Pair<HashMap<Integer, Integer>, Boolean> result, Player player, ShopItem buying, String boughtName) {
        if (!result.getB()) {
            player.sendMessage(AllStrings.PREFIX.get() + AllStrings.CANNOT_AFFORD_TRAP.get());
            GameUtils.buyFailSound(player);
            return false;
        } else {
            GameUtils.makePlayerPay(player.getInventory(), buying.getBuyWith(), buying.getBuyCost(), result.getA());
            GameUtils.paySound(player);
            player.sendMessage(AllStrings.PREFIX.get() + AllStrings.SUCCESSFULLY_ENABLED_TRAP.get() + boughtName);
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
            player.sendMessage(AllStrings.PREFIX.get() + AllStrings.MAX_TRAPS_LIMIT.get());
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

    private void handleStagedUpgrade(StagedUpgrade stagedUpgrade, BedwarsPlayer bedwarsPlayer) {
        UpgradesManager upgradesManager = activeGame.getUpgradesManager();
        TeamData<?> teamData = activeGame.getTeamManager().dataOfBedwarsPlayer(bedwarsPlayer);
        UpgradeType upgradeType = stagedUpgrade.getType();
        int currentLevelForUpgrade = teamData.getUpgradeLevel(upgradeType);
        final List<UpgradeShopItem> levels = stagedUpgrade.getLevels();
        final int levelSize = levels.size();
        if (currentLevelForUpgrade + 1 == levelSize) {
            bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.YOU_ALREADY_HAVE_HIGHEST_UPGRADE_AVAILABLE.get());
            return;
        }


        boolean canUpdateDisplay = levelSize != 2 + currentLevelForUpgrade;

        if (currentLevelForUpgrade + 2 <= levelSize) {
            UpgradeShopItem clicked = levels.get(currentLevelForUpgrade + 1);
            Material buyWith = clicked.getBuyWith();
            int cost = clicked.getPrice();
            PlayerInventory playerInventory = bedwarsPlayer.getInventory();
            boolean canPay = playerInventory.contains(buyWith, cost);

            if (!canPay) {
                bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(buyWith.name()));
                GameUtils.buyFailSound(bedwarsPlayer.getPlayer());
            } else {

                bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.SUCCESSFULLY_UPGRADED_TO_LVL.get() + (2 + currentLevelForUpgrade));
                GameUtils.paySound(bedwarsPlayer.getPlayer());
                GameUtils.payMaterial(buyWith, cost, playerInventory);

                if (canUpdateDisplay) {
                    UpgradeShopItem replaceWith = levels.get(currentLevelForUpgrade + 2);
                    setNextUpgradeItem(stagedUpgrade, replaceWith, teamData);
                    levelUpTeamUpgradeType(teamData, upgradeType);
                } else {
                    markTeamShopUpgradeAsBought(upgradeType, teamData, stagedUpgrade.getSlot());
                }

                upgradesManager.upgrade(bedwarsPlayer, stagedUpgrade.getType(), stagedUpgrade);
            }
        }
    }

    private void setNextUpgradeItem(StagedUpgrade upgrade, UpgradeShopItem nextUpgrade, TeamData<?> data) {
        if (upgrade == null || nextUpgrade == null) {
            return;
        }
        Map<UUID, Inventory> map = activeGame.getGameInventoriesManager().getAssociatedUpgradeGUI();
        data.perform(bedwarsPlayer -> {
            final Inventory playerInv = map.get(bedwarsPlayer.getUniqueId());
            if (playerInv != null) {
                playerInv.setItem(upgrade.getSlot(), nextUpgrade.getCachedFancyStack());
            }
        });
    }

    /**
     * Handle a ShopUpgrade buying procedure.
     * Takes money, plays sound, sends messages,
     * and does the upgrade logic.
     *
     * @param shopUpgrade   The ShopUpgrade to buy.
     * @param bedwarsPlayer The BedwarsPlayer customer.
     */
    private void handleShopUpgrade(ShopUpgrade shopUpgrade, BedwarsPlayer bedwarsPlayer) {
        UpgradesManager upgradesManager = activeGame.getUpgradesManager();
        TeamData<?> playerData = activeGame.getTeamManager().dataOfBedwarsPlayer(bedwarsPlayer);
        UpgradeType upgradeType = shopUpgrade.getType();
        int currentLevelForUpgrade = playerData.getUpgradeLevel(upgradeType);
        if (currentLevelForUpgrade > -1) {
            bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.YOU_ALREADY_HAVE_HIGHEST_UPGRADE_AVAILABLE.get());
            return;
        }

        ShopItem shopItem = shopUpgrade.getShopItem();
        Material buyWith = shopItem.getBuyWith();
        int cost = shopItem.getBuyCost();

        PlayerInventory playerInventory = bedwarsPlayer.getInventory();

        if (playerInventory.contains(buyWith, cost)) { // successfully bought.
            GameUtils.payMaterial(buyWith, cost, playerInventory);
            bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.SUCCESSFULLY_UPGRADED.get() + upgradeType.getDisplayName());
            GameUtils.paySound(bedwarsPlayer.getPlayer());
            upgradesManager.upgrade(bedwarsPlayer, upgradeType, shopUpgrade);
            markTeamShopUpgradeAsBought(upgradeType, playerData, shopUpgrade.getSlot());
        } else {
            bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + AllStrings.YOU_DID_NOT_HAVE_ENOUGH.get() + GameUtils.beautifyCaps(buyWith.name()));
            GameUtils.buyFailSound(bedwarsPlayer.getPlayer());
        }
    }

    private void levelUpTeamUpgradeType(TeamData<?> data, UpgradeType type) {
        Map<UpgradeType, Integer> map = data.getUpgradesShopLevelsMap();
        map.compute(type, (k, v) -> v = (v + 1));
    }

    /**
     * Use the TeamData to update everyone's Upgrade as "Bought".
     * This allows player to know if their team already has bought
     * that one specific upgrade!
     *
     * @param teamData The data of the team to change.
     * @param slot     The clicked slot.
     */
    private void markTeamShopUpgradeAsBought(UpgradeType type, TeamData<?> teamData, int slot) {
        levelUpTeamUpgradeType(teamData, type);
        Map<UUID, Inventory> map = activeGame.getGameInventoriesManager().getAssociatedUpgradeGUI();
        teamData.perform(bp -> {
            final Inventory i = map.get(bp.getUniqueId());
            if (i != null) {
                ItemStack atSlot = i.getItem(slot);
                if (atSlot != null) {
                    ItemMeta meta = atSlot.getItemMeta();
                    final List<String> lore = meta.getLore();
                    lore.add("");
                    lore.add(GameUtils.color("&cYou have bought maximum level!"));
                    meta.setLore(lore);
                    atSlot.setItemMeta(meta);
                }
            }
        });
    }

    private void upgradeLogic(BedwarsPlayer bedwarsPlayer, int clickedSlot) {
        Player player = bedwarsPlayer.getPlayer();

        UpgradeShop upgradeShop = activeGame.getBedwarsGame().getUpgradeShop();
        TrapUpgrades trapUpgrades = upgradeShop.getTrapUpgrades();

        if (clickedSlot != trapUpgrades.getSlot()) {

            for (UpgradeType upgradeType : UpgradeType.values()) {
                Upgrade upgrade = upgradeShop.getUpgrade(upgradeType);

                if (clickedSlot == upgrade.getSlot()) {

                    if (upgrade instanceof StagedUpgrade) {
                        StagedUpgrade stagedUpgrade = (StagedUpgrade) upgrade;
                        handleStagedUpgrade(stagedUpgrade, bedwarsPlayer);

                    } else if (upgrade instanceof ShopUpgrade) {
                        ShopUpgrade shopUpgrade = (ShopUpgrade) upgrade;
                        handleShopUpgrade(shopUpgrade, bedwarsPlayer);
                    }

                    break;
                }
            }
        } else {
            activeGame.getGameInventoriesManager().openTraps(player);
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

        Inventory ui = activeGame.getGameInventoriesManager().getAssociatedUpgradeGUI().get(player.getUniqueId());
        if (ui == null) {
            return;
        }

        Inventory traps = activeGame.getGameInventoriesManager().getAssociatedTrapsGUI().get(player.getUniqueId());

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
