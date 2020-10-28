package me.thevipershow.bedwars.config.objects;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.game.shop.ShopCategory;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class UpgradeItem implements ConfigurationSerializable {

    private final int slot;
    private final int amount;
    private final ShopCategory shopCategory;
    private final List<UpgradeLevel> levels;

    public UpgradeItem(final int slot, final int amount, final ShopCategory shopCategory, final List<UpgradeLevel> levels) {
        this.slot = slot;
        this.amount = amount;
        this.shopCategory = shopCategory;
        this.levels = levels;
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }

    public static UpgradeItem deserialize(final Map<String, Object> map) {
        final int slot = (int) map.get(AllStrings.SLOT.get());
        final int amount = (int) map.get(AllStrings.AMOUNT.get());
        final List<Map<String, Object>> levels = (List<Map<String, Object>>) map.get(AllStrings.LEVELS.get());
        final List<UpgradeLevel> levels_ = levels.stream().map(lvl -> UpgradeLevel.deserialize(lvl)).collect(Collectors.toList());
        final ShopCategory shopCategory = ShopCategory.valueOf((String) map.get(AllStrings.SHOP_CATEGORY.get()));
        return new UpgradeItem(slot, amount, shopCategory, levels_);
    }

    public final int getSlot() {
        return slot;
    }

    public final int getAmount() {
        return amount;
    }

    public final List<UpgradeLevel> getLevels() {
        return levels;
    }

    public final ShopCategory getShopCategory() {
        return shopCategory;
    }
}
