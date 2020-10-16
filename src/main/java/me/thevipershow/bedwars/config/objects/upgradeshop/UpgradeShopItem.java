package me.thevipershow.bedwars.config.objects.upgradeshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.game.AbstractActiveMerchant;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class UpgradeShopItem implements ConfigurationSerializable {
    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("no");
    }

    private final int level;
    private final Material material;
    private final String itemName;
    private final int amount;
    private final int price;
    private final Material buyWith;
    private final List<String> lore;

    private ItemStack cachedFancyStack = null;

    public UpgradeShopItem(int level, Material material, String itemName, int amount, int price, Material buyWith, List<String> lore) {
        this.level = level;
        this.material = material;
        this.itemName = itemName;
        this.amount = amount;
        this.price = price;
        this.buyWith = buyWith;
        this.lore = lore;
    }

    public final ItemStack getCachedFancyStack() {
        if (cachedFancyStack != null) {
            return cachedFancyStack.clone();
        }
        cachedFancyStack = new ItemStack(material, amount);
        final ItemMeta m = cachedFancyStack.getItemMeta();
        m.setDisplayName(itemName);
        final List<String> newLore = new ArrayList<>(this.lore);
        newLore.addAll(AbstractActiveMerchant.priceDescriptorSection(this.price, this.buyWith));
        m.setLore(newLore);
        cachedFancyStack.setItemMeta(m);
        return cachedFancyStack.clone();
    }

    public static UpgradeShopItem deserialize(final Map<String, Object> map) {
        final int level = (int) map.get("level");
        final Material material = Material.valueOf((String) map.get("material"));
        final String itemName = (String) map.get("item-name");
        final int amount = (int) map.get("amount");
        final int price = (int) map.get("price");
        final Material buyWith = Material.valueOf((String) map.get("buy-with"));
        final List<String> lore = (List<String>) map.get("lore");
        return new UpgradeShopItem(level, material, itemName, amount, price, buyWith, lore);
    }

    public int getLevel() {
        return level;
    }

    public Material getMaterial() {
        return material;
    }

    public String getItemName() {
        return itemName;
    }

    public int getAmount() {
        return amount;
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
}
