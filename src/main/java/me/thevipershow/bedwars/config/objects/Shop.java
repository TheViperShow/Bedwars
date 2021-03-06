package me.thevipershow.bedwars.config.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import static me.thevipershow.bedwars.AllStrings.*;

@SerializableAs("Shop")
public class Shop implements ConfigurationSerializable {

    private final String name;
    private final int slots;
    private final List<ShopItem> items;
    private final int glassColor;
    private final List<Integer> glassSlots;
    private final List<UpgradeItem> upgradeItems;
    private final List<PotionItem> potionItem;

    public Shop(String name, int slots, List<ShopItem> items, int glassColor, List<Integer> glassSlots, List<UpgradeItem> upgradeItems, List<PotionItem> potionItem) {
        this.name = name;
        this.slots = slots;
        this.items = items;
        this.glassColor = glassColor;
        this.glassSlots = glassSlots;
        this.upgradeItems = upgradeItems;
        this.potionItem = potionItem;
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("no");
    }

    public static Shop deserialize(Map<String, Object> objectMap) {
        String title = (String) objectMap.get(TITLE.get());
        int slots = (int) objectMap.get(SLOTS.get());
        List<Map<String, Object>> objectMap1 = (List<Map<String, Object>>) objectMap.get(ITEMS.get());
        final List<ShopItem> itemsShop = new ArrayList<>();
        objectMap1.forEach(o -> itemsShop.add(ShopItem.deserialize(o)));
        final Map<String, Object> glass = (Map<String, Object>) objectMap.get(GLASS.get());
        final int glassColor = (int) glass.get(COLOR.get());
        final List<Integer> glassSlots = (List<Integer>) glass.get(SLOTS.get());
        final List<Map<String, Object>> upgradableItems = (List<Map<String, Object>>) objectMap.get(UPGRADABLE_ITEMS.get());
        final List<UpgradeItem> upgradeItems = upgradableItems.stream().map(UpgradeItem::deserialize).collect(Collectors.toList());
        final List<PotionItem> potionItems = ((List<Map<String, Object>>) objectMap.get(POTIONS.get())).stream().map(PotionItem::deserialize).collect(Collectors.toList());
        return new Shop(title, slots, itemsShop, glassColor, glassSlots, upgradeItems, potionItems);
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

    public static final short GREEN_GLASS_DAMAGE = 5;

    public List<Integer> getGlassSlots() {
        return glassSlots;
    }

    public List<UpgradeItem> getUpgradeItems() {
        return upgradeItems;
    }

    public List<PotionItem> getPotionItem() {
        return potionItem;
    }
}
