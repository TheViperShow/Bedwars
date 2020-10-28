package me.thevipershow.bedwars.config.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.game.AbstractActiveMerchant;
import me.thevipershow.bedwars.game.shop.ShopCategory;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import static me.thevipershow.bedwars.AllStrings.*;

public final class PotionItem implements ConfigurationSerializable {

    private final String name;
    private final int slot, amount, price, length, level;
    private final PotionEffectType type;
    private final Material buyWith;
    private final List<String> lore;
    private final ShopCategory shopCategory = ShopCategory.POTIONS;

    private ItemStack cachedFancyStack = null, gameStack = null;

    public PotionItem(String name, int slot, int amount, int price, int length, int level, PotionEffectType type, Material buyWith, List<String> lore) {
        this.name = name;
        this.slot = slot;
        this.amount = amount;
        this.price = price;
        this.length = length;
        this.level = level;
        this.type = type;
        this.buyWith = buyWith;
        this.lore = lore;
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }

    public static PotionItem deserialize(final Map<String, Object> map) {
        final String name = (String) map.get(ITEM_NAME.get());
        final int slot = (int) map.get(SLOT.get());
        final int amount = (int) map.get(AMOUNT.get());
        final int price = (int) map.get(PRICE.get());
        final Material buyWith = Material.valueOf((String) map.get(BUY_WITH.get()));
        final List<String> lore = (List<String>) map.get(LORE.get());
        final PotionEffectType type = PotionEffectType.getByName((String) map.get(TYPE.get()));
        final int length = (int) map.get(LENGTH.get());
        final int level = (int) map.get(LEVEL.get());
        return new PotionItem(name, slot, amount, price, length, level, type, buyWith, lore);
    }

    public ItemStack getCachedFancyStack() {
        if (cachedFancyStack == null) {
            final ItemStack potionStack = new ItemStack(Material.POTION , this.amount);
            final PotionMeta potionMeta = (PotionMeta) potionStack.getItemMeta();
            potionMeta.setMainEffect(type);
            potionMeta.setDisplayName(this.name);
            potionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            final List<String> cLore = new ArrayList<>(getLore());
            cLore.addAll(AbstractActiveMerchant.priceDescriptorSection(getPrice(), getBuyWith()));
            potionMeta.setLore(cLore);
            Potion pot = Potion.fromItemStack(potionStack);
            pot.setType(PotionType.getByEffect(type));
            pot.setLevel(Math.min(PotionType.getByEffect(type).getMaxLevel(), level));
            pot.apply(potionStack);
            potionStack.setItemMeta(potionMeta);
            this.cachedFancyStack = potionStack;
        }
        return cachedFancyStack;
    }

    public ItemStack getGameStack() {
        if (gameStack == null) {
            final ItemStack potionStack = new ItemStack(Material.POTION , this.amount);
            final PotionMeta potionMeta = (PotionMeta) potionStack.getItemMeta();
            potionMeta.setMainEffect(type);
            potionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            potionMeta.setDisplayName(this.name);
            Potion pot = Potion.fromItemStack(potionStack);
            pot.setType(PotionType.getByEffect(type));
            pot.setLevel(Math.min(PotionType.getByEffect(type).getMaxLevel(), level));
            pot.apply(potionStack);
            potionStack.setItemMeta(potionMeta);
            this.gameStack = potionStack;
        }
        return gameStack;
    }

    public String getName() {
        return name;
    }

    public int getSlot() {
        return slot;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public int getLength() {
        return length;
    }

    public int getLevel() {
        return level;
    }

    public PotionEffectType getType() {
        return type;
    }

    public Material getBuyWith() {
        return buyWith;
    }

    public ShopCategory getShopCategory() {
        return shopCategory;
    }

    public List<String> getLore() {
        return lore;
    }
}
