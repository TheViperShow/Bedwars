package me.thevipershow.bedwars.game;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import me.thevipershow.bedwars.config.objects.Shop;
import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.UpgradeLevel;
import me.thevipershow.bedwars.config.objects.upgradeshop.DragonBuffUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.HealPoolUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.IronForgeUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.ManiacMinerUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.ReinforcedArmorUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.SharpnessUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.TrapUpgrades;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShop;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShopItem;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.AlarmTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.BlindnessAndPoisonTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.CounterOffensiveTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.MinerFatigueTrap;
import me.thevipershow.bedwars.game.objects.BedwarsPlayer;
import me.thevipershow.bedwars.game.objects.TeamData;
import me.thevipershow.bedwars.game.shop.ShopCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class GameInventories {
    private final ActiveGame activeGame;
    private final EnderchestManager enderchestManager;
    private final Map<ShopCategory, Inventory> inventories = new EnumMap<>(ShopCategory.class);
    private final Map<UUID, Map<ShopCategory, Inventory>> playerShop = new HashMap<>();
    private final Map<UUID, Inventory> associatedUpgradeGUI = new HashMap<>();
    private final Map<UUID, Inventory> associatedTrapsGUI = new HashMap<>();
    private final Inventory defaultUpgradeInv;
    private final Inventory defaultTrapsInv;

    public GameInventories(ActiveGame activeGame) {
        this.activeGame = activeGame;
        this.enderchestManager = new EnderchestManager();
        setupShopCategories(activeGame.getBedwarsGame().getShop());
        this.defaultTrapsInv = setupTrapsGUIs(activeGame.getBedwarsGame());
        this.defaultUpgradeInv = setupUpgradeGUIs(activeGame.getBedwarsGame());
    }

    public final void assignPlayerShop() {
        for (final TeamData<?> teamData : activeGame.getTeamManager().getDataMap().values()) {
            teamData.perform(bedwarsPlayer -> {
                final Map<ShopCategory, Inventory> shopMap = new EnumMap<>(ShopCategory.class);
                for (final ShopCategory category : ShopCategory.values()) {
                    shopMap.put(category, cloneInventory(inventories.get(category)));
                }
                this.playerShop.put(bedwarsPlayer.getUniqueId(), shopMap);
            });
        }
    }

    public final void updateItemUpgrade(ShopCategory category, int slot, UpgradeLevel newLevel, UUID of) {
        Map<ShopCategory, Inventory> map = this.playerShop.get(of);
        if (map == null) {
            return;
        }
        Inventory inv = map.get(category);
        if (inv == null || newLevel == null) {
            return;
        }
        inv.setItem(slot, newLevel.generateFancyStack());
    }

    public final ShopCategory getOpenShopCategory(UUID uuid, Inventory openInventory) {
        Map<ShopCategory, Inventory> map = this.playerShop.get(uuid);
        if (map != null) {
            label:
            for (final Map.Entry<ShopCategory, Inventory> entry : map.entrySet()) {
                final Inventory value = entry.getValue();
                if (value != null && openInventory != null) {
                    if (value.getType() != openInventory.getType()) {
                        continue;
                    }
                    if (!entry.getKey().getTitle().equals(openInventory.getTitle())) {
                        continue;
                    }
                    final ItemStack[] valueContents = value.getContents();
                    final ItemStack[] openContents = openInventory.getContents();
                    for (int i = 0; i < Math.min(valueContents.length, openContents.length); i++) {
                        final ItemStack vI = valueContents[i];
                        final ItemStack oI = openContents[i];
                        if (oI == null || vI == null) {
                            continue;
                        }
                        if (!oI.isSimilar(vI)) {
                            continue label;
                        }
                    }
                    return entry.getKey();
                }
            }
        }

        return null;
    }

    public final void openShop(Player player) {
        Map<ShopCategory, Inventory> shop = this.playerShop.get(player.getUniqueId());
        if (shop == null) {
            return;
        }
        Inventory toOpen = shop.get(ShopCategory.BLOCKS);
        if (toOpen != null) {
            player.openInventory(toOpen);
        }
    }

    public final void openTraps(Player player) {
        Inventory associated = associatedTrapsGUI.get(player.getUniqueId());
        if (associated == null) {
            associated = cloneInventory(defaultTrapsInv);
            associatedTrapsGUI.put(player.getUniqueId(), associated);
        }
        player.openInventory(associated);
    }

    public final void openUpgrade(Player player) {
        Inventory associated = associatedUpgradeGUI.get(player.getUniqueId());
        if (associated == null) {
            associated = cloneInventory(defaultUpgradeInv);
            associatedUpgradeGUI.put(player.getUniqueId(), associated);
        }
        player.openInventory(associated);
    }

    public static Inventory cloneInventory(Inventory inv) {
        final Inventory inventory = Bukkit.createInventory(inv.getHolder(), inv.getSize(), inv.getTitle());
        inventory.setContents(inv.getContents());
        return inventory;
    }

    private void setupShopCategories(Shop shop) {
        for (final ShopCategory shopCategory : ShopCategory.values()) {
            final Inventory inv = Bedwars.plugin.getServer().createInventory(null, 9 * 6, shopCategory.getTitle());
            shop.getGlassSlots().forEach(slot -> {
                final ItemStack glassStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, shopCategory.ordinal() == slot % 9 ? Shop.GREEN_GLASS_DAMAGE : (short) shop.getGlassColor());
                final ItemMeta meta = glassStack.getItemMeta();
                meta.setDisplayName(" ");
                glassStack.setItemMeta(meta);
                inv.setItem(slot, glassStack);
            });
            for (final ShopCategory value : ShopCategory.values()) {
                inv.setItem(value.ordinal(), value.generateItem());
            }
            inventories.put(shopCategory, inv);
        }

        shop.getItems().forEach(item -> inventories.get(item.getShopCategory()).setItem(item.getSlot(), item.getCachedFancyStack()));
        shop.getPotionItem().forEach(item -> inventories.get(item.getShopCategory()).setItem(item.getSlot(), item.getCachedFancyStack()));
        shop.getUpgradeItems().forEach(item -> inventories.get(item.getShopCategory()).setItem(item.getSlot(), item.getLevels().get(0x00).getCachedFancyStack()));
    }

    public static Inventory setupTrapsGUIs(BedwarsGame bedwarsGame) {
        final Inventory trapsInv = Bukkit.createInventory(null, 36, AllStrings.BEDWARS_TRAPS_TITLE.get());
        final TrapUpgrades trapUpgrades = bedwarsGame.getUpgradeShop().getTrapUpgrades();
        final BlindnessAndPoisonTrap blindnessAndPoisonTrap = trapUpgrades.getBlindnessAndPoisonTrap();
        final CounterOffensiveTrap counterOffensiveTrap = trapUpgrades.getCounterOffensiveTrap();
        final AlarmTrap alarmTrap = trapUpgrades.getAlarmTrap();
        final MinerFatigueTrap minerFatigueTrap = trapUpgrades.getMinerFatigueTrap();

        final ShopItem blindnessAndPoisonItem = blindnessAndPoisonTrap.getShopItem();
        final ShopItem alarmTrapItem = alarmTrap.getShopItem();
        final ShopItem minerFatigueItem = minerFatigueTrap.getShopItem();
        final ShopItem counterOffensiveItem = counterOffensiveTrap.getShopItem();

        trapsInv.setItem(blindnessAndPoisonItem.getSlot(), blindnessAndPoisonItem.getCachedFancyStack());
        trapsInv.setItem(alarmTrapItem.getSlot(), alarmTrapItem.getCachedFancyStack());
        trapsInv.setItem(minerFatigueItem.getSlot(), minerFatigueItem.getCachedFancyStack());
        trapsInv.setItem(counterOffensiveItem.getSlot(), counterOffensiveItem.getCachedFancyStack());

        return getItemStacks(trapsInv);
    }

    private static Inventory getItemStacks(Inventory trapsInv) {
        for (int i = 1; i <= 3; i++) {
            final ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, i);
            final ItemMeta glassMeta = glass.getItemMeta();
            glassMeta.setDisplayName("");
            glass.setItemMeta(glassMeta);
            trapsInv.setItem(29 + i, glass);
        }

        return trapsInv;
    }

    public static Inventory setupUpgradeGUIs(BedwarsGame bedwarsGame) {
        final Inventory upgradeInv = Bukkit.createInventory(null, bedwarsGame.getUpgradeShop().getSlots(), AllStrings.BEDWARS_UPGRADE_TITLE.get());
        final UpgradeShop upgradeShop = bedwarsGame.getUpgradeShop();

        final DragonBuffUpgrade dragonBuffUpgrade = upgradeShop.getDragonBuffUpgrade();
        final HealPoolUpgrade healPoolUpgrade = upgradeShop.getHealPoolUpgrade();
        final IronForgeUpgrade ironForgeUpgrade = upgradeShop.getIronForgeUpgrade();
        final ManiacMinerUpgrade maniacMinerUpgrade = upgradeShop.getManiacMinerUpgrade();
        final ReinforcedArmorUpgrade reinforcedArmorUpgrade = upgradeShop.getReinforcedArmorUpgrade();
        final SharpnessUpgrade sharpnessUpgrade = upgradeShop.getSharpnessUpgrade();

        final ShopItem dragonBuffItem = dragonBuffUpgrade.getShopItem();
        final ShopItem healPoolItem = healPoolUpgrade.getShopItem();
        final UpgradeShopItem ironForgeItem = ironForgeUpgrade.getLevels().get(0);
        final UpgradeShopItem maniacMinerItem = maniacMinerUpgrade.getLevels().get(0);
        final UpgradeShopItem reinforcedArmorItem = reinforcedArmorUpgrade.getLevels().get(0);
        final ShopItem sharpnessItem = sharpnessUpgrade.getShopItem();
        final TrapUpgrades trapUpgrades = upgradeShop.getTrapUpgrades();

        upgradeInv.setItem(dragonBuffItem.getSlot(), dragonBuffItem.getCachedFancyStack());
        upgradeInv.setItem(healPoolItem.getSlot(), healPoolItem.getCachedFancyStack());
        upgradeInv.setItem(ironForgeUpgrade.getSlot(), ironForgeItem.getCachedFancyStack());
        upgradeInv.setItem(maniacMinerUpgrade.getSlot(), maniacMinerItem.getCachedFancyStack());
        upgradeInv.setItem(reinforcedArmorUpgrade.getSlot(), reinforcedArmorItem.getCachedFancyStack());
        upgradeInv.setItem(sharpnessItem.getSlot(), sharpnessItem.getCachedFancyStack());
        upgradeInv.setItem(trapUpgrades.getSlot(), trapUpgrades.getFancyItemStack());

        return getItemStacks(upgradeInv);
    }

    public final EnderchestManager getEnderchestManager() {
        return enderchestManager;
    }

    public final Map<ShopCategory, Inventory> getInventories() {
        return inventories;
    }

    public final Map<UUID, Map<ShopCategory, Inventory>> getPlayerShop() {
        return playerShop;
    }

    public final Map<UUID, Inventory> getAssociatedUpgradeGUI() {
        return associatedUpgradeGUI;
    }

    public final Map<UUID, Inventory> getAssociatedTrapsGUI() {
        return associatedTrapsGUI;
    }

    public final Inventory getDefaultUpgradeInv() {
        return defaultUpgradeInv;
    }

    public final Inventory getDefaultTrapsInv() {
        return defaultTrapsInv;
    }

    public final Inventory getFromCategory(ShopCategory shopCategory) {
        return inventories.get(shopCategory);
    }
}
