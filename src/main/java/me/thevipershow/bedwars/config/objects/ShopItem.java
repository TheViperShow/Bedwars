package me.thevipershow.bedwars.config.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.game.AbstractActiveMerchant;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import static me.thevipershow.bedwars.AllStrings.*;

@SerializableAs("Item")
public class ShopItem implements ConfigurationSerializable {

    private final Material material;
    private final int amount;
    private final Material buyWith;
    private final int buyCost;
    private final int slot;
    private final String itemName;
    private final List<String> lore;

    private ItemStack cachedFancyStack = null;
    private ItemStack cachedGameStack = null;

    public ItemStack generateFancyStack() {
        if (cachedFancyStack != null) return cachedFancyStack.clone();
        final ItemStack stack = new ItemStack(material, amount);
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(itemName);
        final List<String> list = new ArrayList<>(this.lore);
        list.addAll(AbstractActiveMerchant.priceDescriptorSection(this));
        meta.setLore(list);
        stack.setItemMeta(meta);
        this.cachedFancyStack = stack;
        return cachedFancyStack.clone();

    }

    public ShopItem(String material, int amount, String buyWith, int buyCost, int slot, String itemName, List<String> lore) {
        this.material = Material.valueOf(material);
        this.amount = amount;
        this.buyWith = Material.valueOf(buyWith);
        this.buyCost = buyCost;
        this.slot = slot;
        this.itemName = itemName;
        this.lore = lore;
    }

    @Override
    public final Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<>();
        map.put(MATERIAL.get(), material.name());
        map.put(AMOUNT.get(), amount);
        map.put(BUY_WITH.get(), buyWith.name());
        map.put(PRICE.get(), buyCost);
        map.put(SLOT.get(), slot);
        map.put(ITEM_NAME.get(), itemName);
        map.put(LORE.get(), lore);
        return map;
    }

    public static ShopItem deserialize(Map<String, Object> objectMap) {
        String m = (String) objectMap.get(MATERIAL.get());
        int a = (int) objectMap.get(AMOUNT.get());
        String b = (String) objectMap.get(BUY_WITH.get());
        int c = (int) objectMap.get(PRICE.get());
        int s = (int) objectMap.get(SLOT.get());
        String itemName = (String) objectMap.get(ITEM_NAME.get());
        final List<String> lore = (List<String>) objectMap.get(LORE.get());
        return new ShopItem(m, a, b, c, s, itemName, lore);
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public Material getBuyWith() {
        return buyWith;
    }

    public int getBuyCost() {
        return buyCost;
    }

    public int getSlot() {
        return slot;
    }

    public String getItemName() {
        return itemName;
    }

    public ItemStack generateWithoutLore() {
        if (cachedGameStack != null) return cachedGameStack;
        this.cachedGameStack = new ItemStack(material, amount);
        return cachedGameStack;
    }

    public List<String> getLore() {
        return lore;
    }
}
