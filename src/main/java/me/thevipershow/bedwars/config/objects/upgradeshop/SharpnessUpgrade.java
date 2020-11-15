package me.thevipershow.bedwars.config.objects.upgradeshop;

import java.util.Map;
import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.ShopUpgrade;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class SharpnessUpgrade implements ConfigurationSerializable , ShopUpgrade {

    public SharpnessUpgrade(ShopItem item) {
        this.item = item;
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("no");
    }

    public static SharpnessUpgrade deserialize(final Map<String, Object> map) {
        return new SharpnessUpgrade(ShopItem.deserialize(map));
    }

    private final ShopItem item;

    @Override
    public ShopItem getShopItem() {
        return item;
    }

    @Override
    public final UpgradeType getType() {
        return UpgradeType.SHARPNESS;
    }

    @Override
    public final boolean hasStages() {
        return false;
    }

    @Override
    public final int getSlot() {
        return this.item.getSlot();
    }
}
