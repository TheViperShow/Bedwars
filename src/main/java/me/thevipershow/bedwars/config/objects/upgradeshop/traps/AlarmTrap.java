package me.thevipershow.bedwars.config.objects.upgradeshop.traps;

import java.util.Map;
import me.thevipershow.bedwars.config.objects.ShopItem;

public final class AlarmTrap extends AbstractTrap {
    public AlarmTrap(final ShopItem shopItem) {
        super(TrapType.ALARM, shopItem);
    }

    public static AlarmTrap deserialize(final Map<String, Object> map) {
        return new AlarmTrap(ShopItem.deserialize(map));
    }
}
