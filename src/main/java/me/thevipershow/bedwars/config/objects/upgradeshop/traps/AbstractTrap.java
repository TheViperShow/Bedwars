package me.thevipershow.bedwars.config.objects.upgradeshop.traps;

import java.util.Map;
import me.thevipershow.bedwars.config.objects.ShopItem;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class AbstractTrap implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("no");
    }

    private final TrapType trapType;
    private final ShopItem shopItem;

    public AbstractTrap(final TrapType trapType, final ShopItem shopItem) {
        this.trapType = trapType;
        this.shopItem = shopItem;
    }

    public final TrapType getTrapType() {
        return trapType;
    }

    public final ShopItem getShopItem() {
        return shopItem;
    }
}
