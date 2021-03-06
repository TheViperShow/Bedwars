package me.thevipershow.bedwars.config.objects.upgradeshop;

import java.util.Map;
import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.ShopUpgrade;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class DragonBuffUpgrade implements ConfigurationSerializable, ShopUpgrade {

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }

    private final ShopItem shopItem;

    public DragonBuffUpgrade(ShopItem shopItem) {
        this.shopItem = shopItem;
    }

    public static DragonBuffUpgrade deserialize(final Map<String, Object> map) {
        return new DragonBuffUpgrade(ShopItem.deserialize(map));
    }

    @Override
    public ShopItem getShopItem() {
        return shopItem;
    }

    @Override
    public final UpgradeType getType() {
        return UpgradeType.DRAGON_BUFF;
    }

    @Override
    public final boolean hasStages() {
        return false;
    }

    @Override
    public final int getSlot() {
        return this.shopItem.getSlot();
    }
}
