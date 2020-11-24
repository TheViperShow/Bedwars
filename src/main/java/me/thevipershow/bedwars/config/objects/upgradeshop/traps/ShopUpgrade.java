package me.thevipershow.bedwars.config.objects.upgradeshop.traps;

import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.upgradeshop.Upgrade;

public interface ShopUpgrade extends Upgrade {

    ShopItem getShopItem();
}
