package me.thevipershow.aussiebedwars.config.objects.upgradeshop.traps;

import java.util.Map;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;

public final class MinerFatigueTrap extends AbstractTrap {
    public MinerFatigueTrap(final ShopItem shopItem) {
        super(TrapType.MINER_FATIGUE, shopItem);
    }

    public static MinerFatigueTrap deserialize(final Map<String, Object> map) {
        return new MinerFatigueTrap(ShopItem.deserialize(map));
    }
}
