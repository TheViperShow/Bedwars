package me.thevipershow.bedwars.config.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.game.AbstractActiveMerchant;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class UpgradeLevel implements ConfigurationSerializable {

    private final int level;
    private final Material levelMaterial;
    private final String itemName;
    private final int price;
    private final Material buyWith;
    private final List<String> lore;
    private final List<Enchantment> enchants;
    private final ItemStack cachedFancyStack;
    private final ItemStack cachedGameStack;

    public UpgradeLevel(int level, Material levelMaterial, String itemName, int price, Material buyWith, List<String> lore, List<Enchantment> enchants) {
        this.level = level;
        this.levelMaterial = levelMaterial;
        this.itemName = itemName;
        this.price = price;
        this.buyWith = buyWith;
        this.lore = lore;
        this.enchants = enchants;

        final ItemStack fancyStack = new ItemStack(levelMaterial, 1);
        final ItemMeta fancyMeta = fancyStack.getItemMeta();
        final ArrayList<String> newlore = new ArrayList<>(lore);
        newlore.addAll(AbstractActiveMerchant.priceDescriptorSection(price, buyWith));
        fancyMeta.setLore(newlore);
        fancyMeta.setDisplayName(itemName);
        fancyStack.setItemMeta(fancyMeta);
        for (final Enchantment enchant : enchants) {
            fancyStack.addEnchantment(enchant.getEnchant(), enchant.getLevel());
        }
        this.cachedFancyStack = fancyStack;

        final ItemStack gameStack = new ItemStack(levelMaterial, 1);
        final ItemMeta gameMeta = gameStack.getItemMeta();
        gameMeta.setDisplayName(itemName);
        gameStack.setItemMeta(gameMeta);
        for (final Enchantment enchant : enchants) {
            gameStack.addEnchantment(enchant.getEnchant(), enchant.getLevel());
        }

        this.cachedGameStack = gameStack;

    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }

    public static UpgradeLevel deserialize(final Map<String, Object> map) {
        final int level = (int) map.get(AllStrings.LEVEL.get());
        final Material levelMaterial = Material.valueOf((String) map.get(AllStrings.MATERIAL.get()));
        final String itemName = (String) map.get(AllStrings.ITEM_NAME.get());
        final Material buyWith = Material.valueOf((String) map.get(AllStrings.BUY_WITH.get()));
        final int price = (int) map.get(AllStrings.PRICE.get());
        final List<String> lore = (List<String>) map.get(AllStrings.LORE.get());
        final List<Map<String, Object>> enchMap = (List<Map<String, Object>>) map.get(AllStrings.ENCHANTS.get());
        final List<Enchantment> enchants = enchMap.stream().map(Enchantment::deserialize).collect(Collectors.toList());
        return new UpgradeLevel(level, levelMaterial, itemName, price, buyWith, lore, enchants);
    }

    public final ItemStack generateFancyStack() {
        return cachedFancyStack.clone();
    }

    public final ItemStack generateGameStack() {
        return cachedGameStack.clone();
    }


    public int getLevel() {
        return level;
    }

    public Material getLevelMaterial() {
        return levelMaterial;
    }

    public String getItemName() {
        return itemName;
    }

    public int getPrice() {
        return price;
    }

    public Material getBuyWith() {
        return buyWith;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<Enchantment> getEnchants() {
        return enchants;
    }

    public final ItemStack getCachedFancyStack() {
        return cachedFancyStack;
    }

    public final ItemStack getCachedGameStack() {
        return cachedGameStack;
    }
}
