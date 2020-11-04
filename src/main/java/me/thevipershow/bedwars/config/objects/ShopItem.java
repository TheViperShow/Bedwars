package me.thevipershow.bedwars.config.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.game.AbstractActiveMerchant;
import me.thevipershow.bedwars.game.shop.ShopCategory;
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
    private ShopCategory shopCategory = null;

    private ItemStack cachedFancyStack = null;
    private ItemStack cachedGameStack = null;

    public ItemStack getCachedFancyStack() {
        if (cachedFancyStack != null) return cachedFancyStack.clone();
        final ItemStack stack = new ItemStack(material, amount);
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(itemName);
        final List<String> list = new ArrayList<>();
        if (lore != null && !lore.isEmpty()) {
            list.addAll(lore);
        }
        if (list.isEmpty()) {
            list.add("§7Click here to buy!");
        }
        list.addAll(AbstractActiveMerchant.priceDescriptorSection(this));
        meta.setLore(list);
        stack.setItemMeta(meta);
        this.cachedFancyStack = stack;
        return cachedFancyStack.clone();

    }

    public ShopItem(final String material, final int amount, final String buyWith, final int buyCost, final int slot, final String itemName, final List<String> lore, final ShopCategory shopCategory) {
        this.material = Material.valueOf(material);
        this.amount = amount;
        this.buyWith = Material.valueOf(buyWith);
        this.buyCost = buyCost;
        this.slot = slot;
        this.itemName = itemName;
        this.lore = lore;
        this.shopCategory = shopCategory;
    }

    public ShopItem(final String material, final int amount, final String buyWith, final int buyCost, final int slot, final String itemName, final List<String> lore) {
        this.material = Material.valueOf(material);
        this.amount = amount;
        this.buyWith = Material.valueOf(buyWith);
        this.buyCost = buyCost;
        this.slot = slot;
        this.itemName = itemName;
        this.lore = lore;
        this.shopCategory = shopCategory;
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
        map.put(SHOP_CATEGORY.get(), shopCategory.name());
        return map;
    }

    public static ShopItem deserialize(final Map<String, Object> objectMap) {
        final String m = (String) objectMap.get(MATERIAL.get());
        final int a = (int) objectMap.get(AMOUNT.get());
        final String b = (String) objectMap.get(BUY_WITH.get());
        final int c = (int) objectMap.get(PRICE.get());
        final int s = (int) objectMap.get(SLOT.get());
        final String itemName = (String) objectMap.get(ITEM_NAME.get());
        final List<String> lore = (List<String>) objectMap.get(LORE.get());

        final String shopCategoryString = (String) objectMap.get(SHOP_CATEGORY.get());
        if (shopCategoryString != null) {
            return new ShopItem(m, a, b, c, s, itemName, lore, ShopCategory.valueOf(shopCategoryString));
        }
        //final ShopCategory shopCategory = ShopCategory.valueOf(((String) objectMap.get(SHOP_CATEGORY.get())));
        return new ShopItem(m, a, b, c, s, itemName, lore);
    }

    public final Material getMaterial() {
        return material;
    }

    public final int getAmount() {
        return amount;
    }

    public final Material getBuyWith() {
        return buyWith;
    }

    public final int getBuyCost() {
        return buyCost;
    }

    public final int getSlot() {
        return slot;
    }

    public final String getItemName() {
        return itemName;
    }

    public final List<String> getLore() {
        return lore;
    }

    public final ShopCategory getShopCategory() {
        return shopCategory;
    }

    public final ItemStack getCachedGameStack() {
        if (cachedGameStack != null) {
            return cachedGameStack;
        }
        this.cachedGameStack = new ItemStack(material, amount);
        return cachedGameStack.clone();
    }
}
