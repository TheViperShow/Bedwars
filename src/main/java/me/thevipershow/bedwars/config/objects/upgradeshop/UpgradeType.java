package me.thevipershow.bedwars.config.objects.upgradeshop;

public enum UpgradeType {
    DRAGON_BUFF(DragonBuffUpgrade.class),
    HEAL_POOL(HealPoolUpgrade.class),
    IRON_FORGE(IronForgeUpgrade.class),
    MANIAC_MINER(ManiacMinerUpgrade.class),
    REINFORCED_ARMOR(ReinforcedArmorUpgrade.class),
    SHARPNESS(SharpnessUpgrade.class);

    private final Class<? extends Upgrade> clazz;

    UpgradeType(final Class<? extends Upgrade> clazz) {
        this.clazz = clazz;
    }

    public final Class<? extends Upgrade> getClazz() {
        return clazz;
    }
}
