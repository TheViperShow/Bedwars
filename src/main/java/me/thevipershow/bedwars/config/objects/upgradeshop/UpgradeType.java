package me.thevipershow.bedwars.config.objects.upgradeshop;

public enum UpgradeType {

    DRAGON_BUFF(DragonBuffUpgrade.class, "Dragon Buff"),
    HEAL_POOL(HealPoolUpgrade.class, "Heal Pool"),
    IRON_FORGE(IronForgeUpgrade.class, "Iron Forge"),
    MANIAC_MINER(ManiacMinerUpgrade.class, "Maniac Miner"),
    REINFORCED_ARMOR(ReinforcedArmorUpgrade.class, "Reinforced Armor"),
    SHARPNESS(SharpnessUpgrade.class, "Sharpened Swords");

    private final Class<? extends Upgrade> upgradeClass;
    private final String fancyName;

    UpgradeType(final Class<? extends Upgrade> clazz, String fancyName) {
        this.upgradeClass = clazz;
        this.fancyName = fancyName;
    }

    public final Class<? extends Upgrade> getUpgradeClass() {
        return upgradeClass;
    }

    public final String getDisplayName() {
        return fancyName;
    }
}
