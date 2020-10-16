package me.thevipershow.bedwars.config.objects.upgradeshop;

import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class UpgradeShop implements ConfigurationSerializable {

    private final SharpnessUpgrade sharpnessUpgrade;
    private final ReinforcedArmorUpgrade reinforcedArmorUpgrade;
    private final ManiacMinerUpgrade maniacMinerUpgrade;
    private final IronForgeUpgrade ironForgeUpgrade;
    private final HealPoolUpgrade healPoolUpgrade;
    private final DragonBuffUpgrade dragonBuffUpgrade;
    private final TrapUpgrades trapUpgrades;
    private final int slots;

    public UpgradeShop(final SharpnessUpgrade sharpnessUpgrade, final ReinforcedArmorUpgrade reinforcedArmorUpgrade,
                       final ManiacMinerUpgrade maniacMinerUpgrade, final IronForgeUpgrade ironForgeUpgrade,
                       final HealPoolUpgrade healPoolUpgrade, final DragonBuffUpgrade dragonBuffUpgrade,
                       TrapUpgrades trapUpgrades, final int slots) {
        this.sharpnessUpgrade = sharpnessUpgrade;
        this.reinforcedArmorUpgrade = reinforcedArmorUpgrade;
        this.maniacMinerUpgrade = maniacMinerUpgrade;
        this.ironForgeUpgrade = ironForgeUpgrade;
        this.healPoolUpgrade = healPoolUpgrade;
        this.dragonBuffUpgrade = dragonBuffUpgrade;
        this.trapUpgrades = trapUpgrades;
        this.slots = slots;
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("no");
    }

    public static UpgradeShop deserialize(final Map<String, Object> map) {
        final int slots = (int) map.get("slots");
        final SharpnessUpgrade sharpnessUpgrade = SharpnessUpgrade.deserialize((Map<String, Object>) map.get("sharpness"));
        final ReinforcedArmorUpgrade reinforcedArmorUpgrade = ReinforcedArmorUpgrade.deserialize((Map<String, Object>) map.get("reinforced-armor"));
        final ManiacMinerUpgrade maniacMinerUpgrade = ManiacMinerUpgrade.deserialize((Map<String, Object>) map.get("maniac-miner"));
        final IronForgeUpgrade ironForgeUpgrade = IronForgeUpgrade.deserialize((Map<String, Object>) map.get("iron-forge"));
        final HealPoolUpgrade healPoolUpgrade = HealPoolUpgrade.deserialize((Map<String, Object>) map.get("heal-pool"));
        final DragonBuffUpgrade dragonBuffUpgrade = DragonBuffUpgrade.deserialize((Map<String, Object>) map.get("dragon-buff"));
        final TrapUpgrades trapUpgrades = TrapUpgrades.deserialize((Map<String, Object>) map.get("traps"));
        return new UpgradeShop(sharpnessUpgrade, reinforcedArmorUpgrade, maniacMinerUpgrade, ironForgeUpgrade, healPoolUpgrade, dragonBuffUpgrade, trapUpgrades, slots);
    }

    public SharpnessUpgrade getSharpnessUpgrade() {
        return sharpnessUpgrade;
    }

    public ReinforcedArmorUpgrade getReinforcedArmorUpgrade() {
        return reinforcedArmorUpgrade;
    }

    public ManiacMinerUpgrade getManiacMinerUpgrade() {
        return maniacMinerUpgrade;
    }

    public IronForgeUpgrade getIronForgeUpgrade() {
        return ironForgeUpgrade;
    }

    public HealPoolUpgrade getHealPoolUpgrade() {
        return healPoolUpgrade;
    }

    public DragonBuffUpgrade getDragonBuffUpgrade() {
        return dragonBuffUpgrade;
    }

    public int getSlots() {
        return slots;
    }

    public TrapUpgrades getTrapUpgrades() {
        return trapUpgrades;
    }
}
