package me.thevipershow.aussiebedwars.config.objects.upgradeshop;

import java.util.Map;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class SharpnessUpgrade implements ConfigurationSerializable {

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

    public ShopItem getItem() {
        return item;
    }
}
