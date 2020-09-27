package me.thevipershow.aussiebedwars.config.objects.upgradeshop;

import java.util.List;
import java.util.Map;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.traps.AlarmTrap;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.traps.BlindnessAndPoisonTrap;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.traps.CounterOffensiveTrap;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.traps.MinerFatigueTrap;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class TrapUpgrades implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("no");
    }

    private final AlarmTrap alarmTrap;
    private final BlindnessAndPoisonTrap blindnessAndPoisonTrap;
    private final CounterOffensiveTrap counterOffensiveTrap;
    private final MinerFatigueTrap minerFatigueTrap;
    private final String itemName;
    private final List<String> lore;
    private final int slot;
    private final Material material;

    public TrapUpgrades(AlarmTrap alarmTrap, BlindnessAndPoisonTrap blindnessAndPoisonTrap, CounterOffensiveTrap counterOffensiveTrap, MinerFatigueTrap minerFatigueTrap, String itemName, List<String> lore, int slot, Material material) {
        this.alarmTrap = alarmTrap;
        this.blindnessAndPoisonTrap = blindnessAndPoisonTrap;
        this.counterOffensiveTrap = counterOffensiveTrap;
        this.minerFatigueTrap = minerFatigueTrap;
        this.itemName = itemName;
        this.lore = lore;
        this.slot = slot;
        this.material = material;
    }

    public static TrapUpgrades deserialize(final Map<String, Object> map) {
        String itemName = (String) map.get("material");
        int slot = (int) map.get("slot");
        Material material = Material.valueOf((String) map.get("material"));
        List<String> lore = (List<String>) map.get("lore");

        AlarmTrap alarmTrap = AlarmTrap.deserialize((Map<String, Object>) map.get("alarm"));
        BlindnessAndPoisonTrap blindnessAndPoisonTrap = BlindnessAndPoisonTrap.deserialize((Map<String, Object>) map.get("blindness-poison"));
        CounterOffensiveTrap counterOffensiveTrap = CounterOffensiveTrap.deserialize((Map<String, Object>) map.get("counter-offensive"));

        return null; // TODO: Finish when sqce tells me
    }

    public AlarmTrap getAlarmTrap() {
        return alarmTrap;
    }

    public BlindnessAndPoisonTrap getBlindnessAndPoisonTrap() {
        return blindnessAndPoisonTrap;
    }

    public CounterOffensiveTrap getCounterOffensiveTrap() {
        return counterOffensiveTrap;
    }

    public MinerFatigueTrap getMinerFatigueTrap() {
        return minerFatigueTrap;
    }

    public String getItemName() {
        return itemName;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getSlot() {
        return slot;
    }

    public Material getMaterial() {
        return material;
    }
}
