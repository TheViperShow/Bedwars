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
import me.thevipershow.bedwars.game.shop.ShopCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class GameInventories {
    private final EnderchestManager enderchestManager;
    private final Map<ShopCategory, Inventory> inventories = new EnumMap<>(ShopCategory.class);
    private final Map<UUID, Map<ShopCategory, Inventory>> playerShop = new HashMap<>();
    private final Map<UUID, Inventory> associatedUpgradeGUI = new HashMap<>();
    private final Map<UUID, Inventory> associatedTrapsGUI = new HashMap<>();
    private Inventory defaultUpgradeInv, defaultTrapsInv;

    public GameInventories(ActiveGame activeGame) {
        this.enderchestManager = new EnderchestManager();
        setupShopCategories(activeGame.getBedwarsGame().getShop());
        this.defaultTrapsInv = setupTrapsGUIs(activeGame.getBedwarsGame());
        this.defaultUpgradeInv = setupUpgradeGUIs(activeGame.getBedwarsGame());

    }

    public final void openTraps(Player player) {
        Inventory associated = associatedTrapsGUI.get(player.getUniqueId());
        if (associated == null) {
            associated = cloneInventory(defaultUpgradeInv);
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
        final ShopItem healPoolItem = healPoolUpgrade.getItem();
        final UpgradeShopItem ironForgeItem = ironForgeUpgrade.getLevels().get(0);
        final UpgradeShopItem maniacMinerItem = maniacMinerUpgrade.getLevels().get(0);
        final UpgradeShopItem reinforcedArmorItem = reinforcedArmorUpgrade.getLevels().get(0);
        final ShopItem sharpnessItem = sharpnessUpgrade.getItem();
        final TrapUpgrades trapUpgrades = upgradeShop.getTrapUpgrades();

        upgradeInv.setItem(dragonBuffItem.getSlot(), dragonBuffItem.getCachedFancyStack());
        upgradeInv.setItem(healPoolItem.getSlot(), healPoolItem.getCachedFancyStack());
        upgradeInv.setItem(ironForgeUpgrade.getSlot(), ironForgeItem.getCachedFancyStack());
        upgradeInv.setItem(maniacMinerUpgrade.getSlot(), maniacMinerItem.getCachedFancyStack());
        upgradeInv.setItem(reinforcedArmorUpgrade.getSlot(), reinforcedArmorItem.getCachedFancyStack());
        upgradeInv.setItem(sharpnessItem.getSlot(), sharpnessItem.getCachedFancyStack());
        upgradeInv.setItem(trapUpgrades.getSlot(), trapUpgrades.getFancyItemStack());

        for (int i = 1; i <= 3; i++) {
            final ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, i);
            final ItemMeta glassMeta = glass.getItemMeta();
            glassMeta.setDisplayName("");
            glass.setItemMeta(glassMeta);
            upgradeInv.setItem(29 + i, glass);
        }

        return upgradeInv;
    }

    public final Inventory getFromCategory(ShopCategory shopCategory) {
        return inventories.get(shopCategory);
    }
}
