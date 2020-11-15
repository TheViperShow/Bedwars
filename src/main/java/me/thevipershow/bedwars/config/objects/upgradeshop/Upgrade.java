package me.thevipershow.bedwars.config.objects.upgradeshop;

public interface Upgrade {
    UpgradeType getType();

    boolean hasStages();

    int getSlot();
}
