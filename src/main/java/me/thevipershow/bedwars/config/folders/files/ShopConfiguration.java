package me.thevipershow.bedwars.config.folders.files;

import java.io.File;
import static me.thevipershow.bedwars.AllStrings.SHOP;
import me.thevipershow.bedwars.config.folders.ConfigFiles;
import me.thevipershow.bedwars.config.objects.Shop;

public final class ShopConfiguration extends AbstractFileConfig {

    private final Shop shop;

    public ShopConfiguration(File file) {
        super(file, ConfigFiles.SHOP_FILE);
        shop = Shop.deserialize(getMap(SHOP.get()));
    }

    public Shop getShop() {
        return shop;
    }
}
