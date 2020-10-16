package me.thevipershow.bedwars.listeners.game;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.SpawnPosition;
import me.thevipershow.bedwars.config.objects.upgradeshop.DragonBuffUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.HealPoolUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.IronForgeUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.ManiacMinerUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.ReinforcedArmorUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.SharpnessUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.TrapUpgrades;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShop;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShopItem;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeType;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.AlarmTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.BlindnessAndPoisonTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.CounterOffensiveTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.MinerFatigueTrap;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.Pair;
import me.thevipershow.bedwars.game.upgrades.ActiveHealPool;
import me.thevipershow.bedwars.game.upgrades.ActiveTrap;
import me.thevipershow.bedwars.game.upgrades.AlarmActiveTrap;
import me.thevipershow.bedwars.game.upgrades.BlindnessPoisonActiveTrap;
import me.thevipershow.bedwars.game.upgrades.CounterOffensiveActiveTrap;
import me.thevipershow.bedwars.game.upgrades.MinerFatigueActiveTrap;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class UpgradeInteractListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public UpgradeInteractListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private static void maxLevel(final Player player) {
        GameUtils.buyFailSound(player);
        player.sendMessage(Bedwars.PREFIX + "You already have bought maximum level");
    }

    private static boolean payUpgrade(final Pair<HashMap<Integer, Integer>, Boolean> result, final Player player, final Material payWith, final int cost, final String upgradeName) {
        if (!result.getB()) {
            player.sendMessage(Bedwars.PREFIX + "You cannot afford this upgrade.");
            GameUtils.buyFailSound(player);
            return false;
        } else {
            GameUtils.makePlayerPay(player.getInventory(), payWith, cost, result.getA());
            GameUtils.paySound(player);
            player.sendMessage(Bedwars.PREFIX + "You successfully upgraded: §e" + upgradeName);
            return true;
        }
    }

    private static boolean payTraps(final Pair<HashMap<Integer, Integer>, Boolean> result, final Player player, final ShopItem buying, final String boughtName) {
        if (!result.getB()) {
            player.sendMessage(Bedwars.PREFIX + "You cannot afford this trap!");
            GameUtils.buyFailSound(player);
            return false;
        } else {
            GameUtils.makePlayerPay(player.getInventory(), buying.getBuyWith(), buying.getBuyCost(), result.getA());
            GameUtils.paySound(player);
            player.sendMessage(Bedwars.PREFIX + "You successfully enabled trap: §e" + boughtName);
            return true;
        }
    }

    public final void upgradeLogic(final Player player, final int clickedSlot) {
        final BedwarsTeam pTeam = activeGame.getPlayerTeam(player);
        final PlayerInventory playerInventory = player.getInventory();

        final UpgradeShop upgradeShop = activeGame.getBedwarsGame().getUpgradeShop();
        final DragonBuffUpgrade dragonBuffUpgrade = upgradeShop.getDragonBuffUpgrade();
        final HealPoolUpgrade healPoolUpgrade = upgradeShop.getHealPoolUpgrade();
        final IronForgeUpgrade ironForgeUpgrade = upgradeShop.getIronForgeUpgrade();
        final ManiacMinerUpgrade maniacMinerUpgrade = upgradeShop.getManiacMinerUpgrade();
        final ReinforcedArmorUpgrade reinforcedArmorUpgrade = upgradeShop.getReinforcedArmorUpgrade();
        final SharpnessUpgrade sharpnessUpgrade = upgradeShop.getSharpnessUpgrade();
        final TrapUpgrades trapUpgrades = upgradeShop.getTrapUpgrades();

        final EnumMap<UpgradeType, Map<BedwarsTeam, Integer>> upgradesAvailable = activeGame.getUpgradesLevelsMap();

        if (clickedSlot == trapUpgrades.getSlot()) {
            activeGame.openTraps(player);
        } else if (clickedSlot == dragonBuffUpgrade.getShopItem().getSlot()) {
            final int currentLevel = upgradesAvailable.get(dragonBuffUpgrade.getType()).get(pTeam);
            if (currentLevel == 1) {
                maxLevel(player);
            } else {
                final ShopItem shopItem = dragonBuffUpgrade.getShopItem();
                Pair<HashMap<Integer, Integer>, Boolean> pay = GameUtils.canAfford(playerInventory, shopItem.getBuyWith(), shopItem.getBuyCost());
                if (payUpgrade(pay, player, shopItem.getBuyWith(), shopItem.getBuyCost(), "Dragon Buff")) { // This is the Dragon Buff Logic
                    upgradesAvailable.get(dragonBuffUpgrade.getType()).put(pTeam, 1);                           // Here they have correctly upgrade their item.
                }
            }
        } else if (clickedSlot == healPoolUpgrade.getItem().getSlot()) {
            final Map<BedwarsTeam, Integer> healPoolTeamLevelsMap = Objects.requireNonNull(upgradesAvailable.get(healPoolUpgrade.getType()));
            final int currentLevel = Objects.requireNonNull(healPoolTeamLevelsMap.get(pTeam));
            if (currentLevel == 1) {
                maxLevel(player);
            } else {
                final ShopItem shopItem = healPoolUpgrade.getItem();
                Pair<HashMap<Integer, Integer>, Boolean> pay = GameUtils.canAfford(playerInventory, shopItem.getBuyWith(), shopItem.getBuyCost());
                if (payUpgrade(pay, player, shopItem.getBuyWith(), shopItem.getBuyCost(), "Heal Pool")) { // This is the Heal Pool Logic
                    upgradesAvailable.get(healPoolUpgrade.getType()).put(pTeam, 1);                           // Here they have correctly upgraded their item.
                    final ActiveHealPool activeHealPool = new ActiveHealPool(activeGame, pTeam, healPoolUpgrade);
                    activeGame.getHealPools().add(activeHealPool);
                    activeHealPool.start();

                    upgradesAvailable.get(healPoolUpgrade.getType()).put(pTeam, 1);
                }
            }
        } else if (clickedSlot == ironForgeUpgrade.getSlot()) {
            final int currentLevel = upgradesAvailable.get(ironForgeUpgrade.getType()).get(pTeam);
            final int maxLevel = ironForgeUpgrade.getLevels().size();
            if (currentLevel == maxLevel) {
                maxLevel(player);
            } else {
                final UpgradeShopItem itemToBuy = ironForgeUpgrade.getLevels().get(currentLevel);
                final Pair<HashMap<Integer, Integer>, Boolean> pay = GameUtils.canAfford(playerInventory, itemToBuy.getBuyWith(), itemToBuy.getPrice());
                if (payUpgrade(pay, player, itemToBuy.getBuyWith(), itemToBuy.getPrice(), "Iron Forge")) { // This is the Iron Forge Logic.
                    upgradesAvailable.get(ironForgeUpgrade.getType()).put(pTeam, currentLevel + 1);            // Here they have correctly upgraded their item.

                    if (currentLevel + 1 == 3) {

                        activeGame.getTeamSpawners(pTeam).stream().findAny().ifPresent(s -> {
                            final SpawnPosition spawn = s.getSpawner().getSpawnPosition();
                            final Location spawnLoc = spawn.add(0.00, 0.05, 0.00).toLocation(activeGame.getAssociatedWorld());
                            activeGame.getEmeraldBoostDrops().add(
                                    activeGame.getPlugin().getServer().getScheduler().runTaskTimer(activeGame.getPlugin(),
                                            () -> activeGame.getAssociatedWorld().dropItem(spawnLoc, new ItemStack(Material.EMERALD, 1)).setVelocity(new Vector(0,0,0)), 20L, 20L * 180L)
                            );
                        });

                    } else {

                        activeGame.getTeamSpawners(pTeam).forEach(s -> {
                            s.setDropSpeedRegulator((currentLevel + 1) * 50);
                            System.out.println(s.getSpawner().getSpawnPosition().toString());
                        });

                    }


                    activeGame.getAssociatedUpgradeGUI().get(player.getUniqueId()).setItem(clickedSlot, ironForgeUpgrade.getLevels().get(Math.min(4, currentLevel + 1)).getCachedFancyStack());
                    player.updateInventory();
                }
            }
        } else if (clickedSlot == maniacMinerUpgrade.getSlot()) {
            final int currentLevel = upgradesAvailable.get(maniacMinerUpgrade.getType()).get(pTeam);
            final int maxLevel = maniacMinerUpgrade.getLevels().size();
            if (currentLevel == maxLevel) {
                maxLevel(player);
            } else {
                final UpgradeShopItem itemToBuy = maniacMinerUpgrade.getLevels().get(currentLevel);
                final Pair<HashMap<Integer, Integer>, Boolean> pay = GameUtils.canAfford(playerInventory, itemToBuy.getBuyWith(), itemToBuy.getPrice());
                if (payUpgrade(pay, player, itemToBuy.getBuyWith(), itemToBuy.getPrice(), "Maniac Miner")) { // This is the Maniac Miner Logic
                    upgradesAvailable.get(maniacMinerUpgrade.getType()).put(pTeam, currentLevel + 1);            //  Here they have correctly upgraded their item.
                    activeGame.getTeamPlayers(pTeam).forEach(p -> {
                        if (!activeGame.isOutOfGame(p) && p.isOnline()) {
                            p.removePotionEffect(PotionEffectType.FAST_DIGGING);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 69420, currentLevel));
                        }
                    });

                    activeGame.getAssociatedUpgradeGUI().get(player.getUniqueId()).setItem(clickedSlot, maniacMinerUpgrade.getLevels().get(Math.min(4, currentLevel + 1)).getCachedFancyStack());
                    player.updateInventory();
                }
            }
        } else if (clickedSlot == reinforcedArmorUpgrade.getSlot()) {
            final int currentLevel = upgradesAvailable.get(reinforcedArmorUpgrade.getType()).get(pTeam);
            final int maxLevel = reinforcedArmorUpgrade.getLevels().size();
            if (currentLevel == maxLevel) {
                maxLevel(player);
            } else {
                final UpgradeShopItem itemToBuy = reinforcedArmorUpgrade.getLevels().get(currentLevel);
                final Pair<HashMap<Integer, Integer>, Boolean> pay = GameUtils.canAfford(playerInventory, itemToBuy.getBuyWith(), itemToBuy.getPrice());
                if (payUpgrade(pay, player, itemToBuy.getBuyWith(), itemToBuy.getPrice(), "Reinforced Armor")) {// This is the Reinforced Armor Logic
                    upgradesAvailable.get(reinforcedArmorUpgrade.getType()).put(pTeam, currentLevel + 1);           //  Here they have correctly upgraded their item.

                    activeGame.getTeamPlayers(pTeam).forEach(p -> {
                        if (p.isOnline() && !activeGame.isOutOfGame(p)) {                                           // Enchanting the armor of everyone on the team
                            GameUtils.enchantArmor(Enchantment.PROTECTION_ENVIRONMENTAL, Math.min(4, currentLevel + 1), p); // To their level
                        }
                    });

                    activeGame.getAssociatedUpgradeGUI().get(player.getUniqueId()).setItem(clickedSlot, reinforcedArmorUpgrade.getLevels().get(currentLevel + 1).getCachedFancyStack());
                    player.updateInventory();
                }
            }
        } else if (clickedSlot == sharpnessUpgrade.getItem().getSlot()) {
            final int currentLevel = upgradesAvailable.get(sharpnessUpgrade.getType()).get(pTeam);
            if (currentLevel == 1) {
                maxLevel(player);
            } else {
                final ShopItem shopItem = sharpnessUpgrade.getItem();
                Pair<HashMap<Integer, Integer>, Boolean> pay = GameUtils.canAfford(playerInventory, shopItem.getBuyWith(), shopItem.getBuyCost());
                if (payUpgrade(pay, player, shopItem.getBuyWith(), shopItem.getBuyCost(), "Sharpened Swords")) {  // This is the Sharpened Swords Logic
                    upgradesAvailable.get(sharpnessUpgrade.getType()).put(pTeam, 1);                                    // Here they have correctly upgrade their item.

                    activeGame.getTeamPlayers(pTeam).forEach(p -> {
                        if (p.isOnline() && !activeGame.isOutOfGame(p)) {
                            GameUtils.enchantSwords(Enchantment.DAMAGE_ALL, 1, p);
                        }
                    });
                }
            }
        }
    }

    private void addTrap(final Player player, Class<? extends ActiveTrap> activeTrapClass, final BedwarsTeam playerTeam, final int slot, final ShopItem item) {
        try {
            activeGame.getTeamActiveTrapsList().get(playerTeam).addLast(activeTrapClass.getConstructor(BedwarsTeam.class, ActiveGame.class).newInstance(playerTeam, activeGame));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        activeGame.getAssociatedTrapsGUI().get(player.getUniqueId()).setItem(slot, item.generateFancyStack());
        activeGame.getAssociatedUpgradeGUI().get(player.getUniqueId()).setItem(slot, item.generateFancyStack());
    }

    private void trapLogic(final Player player, final int clickedSlot) {
        final UpgradeShop upgradeShop = activeGame.getBedwarsGame().getUpgradeShop();
        final TrapUpgrades trapUpgrades = upgradeShop.getTrapUpgrades();

        final AlarmTrap alarmTrap = trapUpgrades.getAlarmTrap();
        final BlindnessAndPoisonTrap blindnessAndPoisonTrap = trapUpgrades.getBlindnessAndPoisonTrap();
        final CounterOffensiveTrap counterOffensiveTrap = trapUpgrades.getCounterOffensiveTrap();
        final MinerFatigueTrap minerFatigueTrap = trapUpgrades.getMinerFatigueTrap();

        final ShopItem alarmShopItem = alarmTrap.getShopItem();
        final ShopItem blindnessAndPoisonShopItem = blindnessAndPoisonTrap.getShopItem();
        final ShopItem counterOffensiveShopItem = counterOffensiveTrap.getShopItem();
        final ShopItem minerFatigueShopItem = minerFatigueTrap.getShopItem();

        final BedwarsTeam playerTeam = activeGame.getPlayerTeam(player);
        final LinkedList<ActiveTrap> activeTeamTraps = activeGame.getTeamActiveTrapsList().get(playerTeam);
        final int slotToSet = 30 + activeTeamTraps.size();

        if (activeTeamTraps.size() >= 0x03) {
            player.sendMessage(Bedwars.PREFIX + "You have reached maximum traps limit.");
            player.playSound(player.getLocation(), Sound.ARROW_HIT, 7.5f, 0.85f);
        } else if (alarmShopItem.getSlot() == clickedSlot) {

            final Pair<HashMap<Integer, Integer>, Boolean> payment = GameUtils.canAfford(player.getInventory(), alarmShopItem.getBuyWith(), alarmShopItem.getBuyCost());
            if (payTraps(payment, player, alarmShopItem, "Alarm Trap")) {
                addTrap(player, AlarmActiveTrap.class, playerTeam, slotToSet, alarmShopItem);
            }

        } else if (blindnessAndPoisonShopItem.getSlot() == clickedSlot) {

            final Pair<HashMap<Integer, Integer>, Boolean> payment = GameUtils.canAfford(player.getInventory(), blindnessAndPoisonShopItem.getBuyWith(), blindnessAndPoisonShopItem.getBuyCost());
            if (payTraps(payment, player, blindnessAndPoisonShopItem, "Blindness-Poison Trap")) {
                addTrap(player, BlindnessPoisonActiveTrap.class, playerTeam, slotToSet, blindnessAndPoisonShopItem);
            }

        } else if (counterOffensiveShopItem.getSlot() == clickedSlot) {

            final Pair<HashMap<Integer, Integer>, Boolean> payment = GameUtils.canAfford(player.getInventory(), counterOffensiveShopItem.getBuyWith(), counterOffensiveShopItem.getBuyCost());
            if (payTraps(payment, player, counterOffensiveShopItem, "Counter-Offensive Trap")) {
                addTrap(player, CounterOffensiveActiveTrap.class, playerTeam, slotToSet, counterOffensiveShopItem);
            }

        } else if (minerFatigueShopItem.getSlot() == clickedSlot) {

            final Pair<HashMap<Integer, Integer>, Boolean> payment = GameUtils.canAfford(player.getInventory(), minerFatigueShopItem.getBuyWith(), minerFatigueShopItem.getBuyCost());
            if (payTraps(payment, player, minerFatigueShopItem, "Miner-Fatigue Trap")) {
                addTrap(player, MinerFatigueActiveTrap.class, playerTeam, slotToSet, minerFatigueShopItem);
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(final InventoryClickEvent event) {
        final HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }
        final Player player = (Player) entity;
        if (!player.getWorld().equals(activeGame.getAssociatedWorld())) {
            return;
        }

        final Inventory ui = activeGame.getAssociatedUpgradeGUI().get(player.getUniqueId());
        if (ui == null) {
            return;
        }

        final Inventory traps = activeGame.getAssociatedTrapsGUI().get(player.getUniqueId());

        if (event.getClickedInventory() == null) {
            return;
        }

        final Inventory topInventory = event.getView().getTopInventory();

        if (ui != null && ui.equals(topInventory)) {
            event.setCancelled(true);
            final int clickedSlot = event.getSlot();
            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) {
                return;
            }
            upgradeLogic(player, clickedSlot);
        } else if (traps != null && traps.equals(topInventory)) {
            event.setCancelled(true);
            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) {
                return;
            }
            final int clickedSlot = event.getSlot();
            trapLogic(player, clickedSlot);
        }
    }
}
