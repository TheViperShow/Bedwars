package me.thevipershow.aussiebedwars.config.objects.upgradeshop;

import java.util.Map;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class HealPoolUpgrade implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("no");
    }

    private final ShopItem item;

    public HealPoolUpgrade(ShopItem item) {
        this.item = item;
    }

    public static HealPoolUpgrade deserialize(final Map<String, Object> map) {
        return new HealPoolUpgrade(ShopItem.deserialize(map));
    }

    public ShopItem getItem() {
        return item;
    }
}
