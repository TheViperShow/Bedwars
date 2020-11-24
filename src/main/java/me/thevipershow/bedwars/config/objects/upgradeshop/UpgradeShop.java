package me.thevipershow.bedwars.config.objects.upgradeshop;

import java.util.Map;
import me.thevipershow.bedwars.config.folders.files.AbstractFileConfig;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import static me.thevipershow.bedwars.AllStrings.*;
import sun.security.provider.SHA;

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
        throw new UnsupportedOperationException();
    }

    public static UpgradeShop deserialize(final Map<String, Object> map) {
        final int slots = (int) map.get(SLOTS.get());
        final SharpnessUpgrade sharpnessUpgrade = SharpnessUpgrade.deserialize((Map<String, Object>) map.get(SHARPNESS.get()));
        final ReinforcedArmorUpgrade reinforcedArmorUpgrade = ReinforcedArmorUpgrade.deserialize((Map<String, Object>) map.get(REINFORCED_ARMOR.get()));
        final ManiacMinerUpgrade maniacMinerUpgrade = ManiacMinerUpgrade.deserialize((Map<String, Object>) map.get(MANIAC_MINER.get()));
        final IronForgeUpgrade ironForgeUpgrade = IronForgeUpgrade.deserialize((Map<String, Object>) map.get(IRON_FORGE.get()));
        final HealPoolUpgrade healPoolUpgrade = HealPoolUpgrade.deserialize((Map<String, Object>) map.get(HEAL_POOL.get()));
        final DragonBuffUpgrade dragonBuffUpgrade = DragonBuffUpgrade.deserialize((Map<String, Object>) map.get(DRAGON_BUFF.get()));
        final Object tU = map.get(TRAPS.get());
        System.out.println(tU.toString());
        final TrapUpgrades trapUpgrades = TrapUpgrades.deserialize(AbstractFileConfig.removeMemorySections((Map<String, Object>) tU));
        return new UpgradeShop(sharpnessUpgrade, reinforcedArmorUpgrade, maniacMinerUpgrade, ironForgeUpgrade, healPoolUpgrade, dragonBuffUpgrade, trapUpgrades, slots);
    }

    public final SharpnessUpgrade getSharpnessUpgrade() {
        return sharpnessUpgrade;
    }

    public final ReinforcedArmorUpgrade getReinforcedArmorUpgrade() {
        return reinforcedArmorUpgrade;
    }

    public final ManiacMinerUpgrade getManiacMinerUpgrade() {
        return maniacMinerUpgrade;
    }

    public final IronForgeUpgrade getIronForgeUpgrade() {
        return ironForgeUpgrade;
    }

    public final HealPoolUpgrade getHealPoolUpgrade() {
        return healPoolUpgrade;
    }

    public final DragonBuffUpgrade getDragonBuffUpgrade() {
        return dragonBuffUpgrade;
    }

    public final int getSlots() {
        return slots;
    }

    public final TrapUpgrades getTrapUpgrades() {
        return trapUpgrades;
    }

    public final <T extends Upgrade> T getUpgrade(UpgradeType upgradeType) {
        switch (upgradeType) {
            case HEAL_POOL:
                return (T) this.healPoolUpgrade;
            case SHARPNESS:
                return (T) this.sharpnessUpgrade;
            case IRON_FORGE:
                return (T) this.ironForgeUpgrade;
            case DRAGON_BUFF:
                return (T) this.dragonBuffUpgrade;
            case MANIAC_MINER:
                return (T) this.maniacMinerUpgrade;
            case REINFORCED_ARMOR:
                return (T) this.reinforcedArmorUpgrade;
        }
        return null;
    }
}
