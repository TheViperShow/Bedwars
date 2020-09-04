package me.thevipershow.aussiebedwars.config.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Shop")
public class Shop implements ConfigurationSerializable {

    private final String name;
    private final int slots;
    private final List<ShopItem> items;

    public Shop(String name, int slots, List<ShopItem> items) {
        this.name = name;
        this.slots = slots;
        this.items = items;
    }

    @Override
    public Map<String, Object> serialize() {
        return null; //TODO: im not going to write this.
    }


    public static Shop deserialize(Map<String, Object> objectMap) {
        String title = (String) objectMap.get("title");
        int slots = (int) objectMap.get("slots");
        List<Map<String, Object>> objectMap1 = (List<Map<String, Object>>) objectMap.get("items");
        final List<ShopItem> itemsShop = new ArrayList<>();
        objectMap1.forEach(o -> itemsShop.add(ShopItem.deserialize(o)));
        return new Shop(title, slots, itemsShop);
    }
}
