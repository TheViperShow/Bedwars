package me.thevipershow.bedwars.config.objects.upgradeshop.traps;

import java.util.Map;
import me.thevipershow.bedwars.config.objects.ShopItem;

public final class BlindnessAndPoisonTrap extends AbstractTrap {

    public BlindnessAndPoisonTrap(final ShopItem shopItem) {
        super(TrapType.BLINDNESS_AND_POISON, shopItem);
    }

    public static BlindnessAndPoisonTrap deserialize(final Map<String, Object> map) {
        return new BlindnessAndPoisonTrap(ShopItem.deserialize(map));
    }
}
