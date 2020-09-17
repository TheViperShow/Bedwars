package me.thevipershow.aussiebedwars.config.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.game.AbstractActiveMerchant;
import net.minecraft.server.v1_8_R3.Items;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Item;
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

    public UpgradeLevel(int level, Material levelMaterial, String itemName, int price, Material buyWith, List<String> lore, List<Enchantment> enchants) {
        this.level = level;
        this.levelMaterial = levelMaterial;
        this.itemName = itemName;
        this.price = price;
        this.buyWith = buyWith;
        this.lore = lore;
        this.enchants = enchants;
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("no");
    }

    public static UpgradeLevel deserialize(final Map<String, Object> map) {
        final int level = (int) map.get("level");
        final Material levelMaterial = Material.valueOf((String) map.get("material"));
        final String itemName = (String) map.get("item-name");
        final Material buyWith = Material.valueOf((String) map.get("buy-with"));
        final int price = (int) map.get("price");
        final List<String> lore = (List<String>) map.get("lore");
        final List<Map<String, Object>> enchMap = (List<Map<String, Object>>) map.get("enchantments");
        final List<Enchantment> enchants = enchMap.stream().map(Enchantment::deserialize).collect(Collectors.toList());
        return new UpgradeLevel(level, levelMaterial, itemName, price, buyWith, lore, enchants);
    }

    public final ItemStack generateFancyStack() {
        final ItemStack stack = generateGameStack();
        final ItemMeta meta = stack.getItemMeta();
        final List<String> lore = new ArrayList<>(this.lore);
        lore.addAll(AbstractActiveMerchant.priceDescriptorSection(this));
        stack.setItemMeta(meta);
        return stack;
    }

    public final ItemStack generateGameStack() {
        final ItemStack stack = new ItemStack(levelMaterial, 1);
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(itemName);
        stack.setItemMeta(meta);
        for (final Enchantment enchant : enchants) {
            stack.addEnchantment(enchant.getEnchant(), enchant.getLevel());
        }
        return stack;
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
}
