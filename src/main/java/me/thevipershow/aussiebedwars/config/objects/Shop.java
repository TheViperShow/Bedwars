package me.thevipershow.aussiebedwars.config.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Shop")
public class Shop implements ConfigurationSerializable {

    private final String name;
    private final int slots;
    private final List<ShopItem> items;
    private final int glassColor;
    private final List<Integer> glassSlots;
    private final List<UpgradeItem> upgradeItems;

    public Shop(String name, int slots, List<ShopItem> items, int glassColor, List<Integer> glassSlots, List<UpgradeItem> upgradeItems) {
        this.name = name;
        this.slots = slots;
        this.items = items;
        this.glassColor = glassColor;
        this.glassSlots = glassSlots;
        this.upgradeItems = upgradeItems;

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
        final Map<String, Object> glass = (Map<String, Object>) objectMap.get("glass");
        final int glassColor = (int) glass.get("color");
        final List<Integer> glassSlots = (List<Integer>) glass.get("slots");
        final List<Map<String, Object>> upgradableItems = (List<Map<String, Object>>) objectMap.get("upgradable-items");
        final List<UpgradeItem> upgradeItems = upgradableItems.stream().map(UpgradeItem::deserialize).collect(Collectors.toList());
        return new Shop(title, slots, itemsShop, glassColor, glassSlots, upgradeItems);
    }

    public String getName() {
        return name;
    }

    public int getSlots() {
        return slots;
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public int getGlassColor() {
        return glassColor;
    }

    public List<Integer> getGlassSlots() {
        return glassSlots;
    }

    public List<UpgradeItem> getUpgradeItems() {
        return upgradeItems;
    }

}
