package me.thevipershow.bedwars.config.objects.upgradeshop;

import java.util.List;

public interface StagedUpgrade extends Upgrade {

    List<UpgradeShopItem> getLevels();
}
