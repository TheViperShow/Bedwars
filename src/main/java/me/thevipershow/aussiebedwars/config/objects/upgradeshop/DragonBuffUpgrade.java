package me.thevipershow.aussiebedwars.config.objects.upgradeshop;

import java.util.Map;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class DragonBuffUpgrade implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("no");
    }

    private final ShopItem shopItem;

    public DragonBuffUpgrade(ShopItem shopItem) {
        this.shopItem = shopItem;
    }

    public static DragonBuffUpgrade deserialize(final Map<String, Object> map) {
        return new DragonBuffUpgrade(ShopItem.deserialize(map));
    }

    public ShopItem getShopItem() {
        return shopItem;
    }
}