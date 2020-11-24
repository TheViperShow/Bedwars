package me.thevipershow.bedwars.config.folders.files;

import java.io.File;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.config.folders.ConfigFiles;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShop;

public final class UpgradesConfiguration extends AbstractFileConfig {

    private final UpgradeShop upgradeShop;

    public UpgradesConfiguration(File file) {
        super(file, ConfigFiles.UPGRADES_FILE);
        this.upgradeShop = UpgradeShop.deserialize(getMap(AllStrings.UPGRADES.get()));
    }

    public UpgradeShop getUpgradeShop() {
        return upgradeShop;
    }
}
