package me.thevipershow.aussiebedwars.bedwars.objects.shops;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Item")
public class ShopItem implements ConfigurationSerializable {

    private final Material material;
    private final int amount;
    private final Material buyWith;
    private final int buyCost;
    private final int slot;

    public ShopItem(String material, int amount, String buyWith, int buyCost, int slot) {
        this.material = Material.valueOf(material);
        this.amount = amount;
        this.buyWith = Material.valueOf(buyWith);
        this.buyCost = buyCost;
        this.slot = slot;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<>();
        map.put("material", material.name());
        map.put("amount", amount);
        map.put("buy-with", buyWith.name());
        map.put("price", buyCost);
        return map;
    }

    public static ShopItem deserialize(Map<String, Object> objectMap) {
        String m = (String) objectMap.get("material");
        int a = (int) objectMap.get("amount");
        String b = (String) objectMap.get("buy-with");
        int c = (int) objectMap.get("price");
        int s = (int) objectMap.get("slot");
        return new ShopItem(m, a, b, c, s);
    }
}
