package me.thevipershow.bedwars.config.objects.upgradeshop.traps;

import java.util.Map;
import me.thevipershow.bedwars.config.objects.ShopItem;

public final class CounterOffensiveTrap extends AbstractTrap {
    public CounterOffensiveTrap(final ShopItem shopItem) {
        super(TrapType.COUNTER_OFFENSIVE, shopItem);
    }

    public static CounterOffensiveTrap deserialize(final Map<String, Object> map) {
        return new CounterOffensiveTrap(ShopItem.deserialize(map));
    }
}
