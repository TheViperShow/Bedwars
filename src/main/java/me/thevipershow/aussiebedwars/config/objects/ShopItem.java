package me.thevipershow.aussiebedwars.config.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

@SerializableAs("Item")
public class ShopItem implements ConfigurationSerializable {

    private final Material material;
    private final int amount;
    private final Material buyWith;
    private final int buyCost;
    private final int slot;
    private final String itemName;
    private final List<String> lore;

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
        map.put("material", material.name());
        map.put("amount", amount);
        map.put("buy-with", buyWith.name());
        map.put("price", buyCost);
        map.put("slot", slot);
        map.put("item-name", itemName);
        map.put("lore", lore);
        return map;
    }

    public static ShopItem deserialize(Map<String, Object> objectMap) {
        String m = (String) objectMap.get("material");
        int a = (int) objectMap.get("amount");
        String b = (String) objectMap.get("buy-with");
        int c = (int) objectMap.get("price");
        int s = (int) objectMap.get("slot");
        String itemName = (String) objectMap.get("item-name");
        final List<String> lore = (List<String>) objectMap.get("lore");
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
        return new ItemStack(this.getMaterial(), this.amount);
    }

    public List<String> getLore() {
        return lore;
    }
}
