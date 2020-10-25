package me.thevipershow.bedwars.config.objects.upgradeshop;

import java.util.Map;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.config.objects.ShopItem;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class
HealPoolUpgrade implements ConfigurationSerializable , Upgrade {

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }

    private final ShopItem item;
    private final int healRadius;
    private final int healFrequency;
    private final double healAmount;

    public HealPoolUpgrade(ShopItem item, int healRadius, int healFrequency, double healAmount) {
        this.item = item;
        this.healRadius = healRadius;
        this.healFrequency = healFrequency;
        this.healAmount = healAmount;
    }

    public static HealPoolUpgrade deserialize(final Map<String, Object> map) {
        final int healRadius = (int) map.get(AllStrings.HEAL_RADIUS.get());
        final int healFrequency = (int) map.get(AllStrings.HEAL_FREQUENCY.get());
        final double healAmount = (double) map.get(AllStrings.HEAL_AMOUNT.get());
        return new HealPoolUpgrade(ShopItem.deserialize(map), healRadius, healFrequency, healAmount);
    }

    public final ShopItem getItem() {
        return item;
    }

    public final int getHealRadius() {
        return healRadius;
    }

    public final int getHealFrequency() {
        return healFrequency;
    }

    public final double getHealAmount() {
        return healAmount;
    }

    @Override
    public final UpgradeType getType() {
        return UpgradeType.HEAL_POOL;
    }
}
