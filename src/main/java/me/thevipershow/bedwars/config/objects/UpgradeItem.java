package me.thevipershow.bedwars.config.objects;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.AllStrings;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class UpgradeItem implements ConfigurationSerializable {

    private final int slot;
    private final int amount;
    private final List<UpgradeLevel> levels;

    public UpgradeItem(int slot, int amount, List<UpgradeLevel> levels) {
        this.slot = slot;
        this.amount = amount;
        this.levels = levels;
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }

    public static UpgradeItem deserialize(final Map<String, Object> map) {
        final int slot = (int) map.get(AllStrings.SLOT.get());
        final int amount = (int) map.get(AllStrings.AMOUNT.get());
        final List<Map<String, Object>> levels = (List<Map<String, Object>>) map.get(AllStrings.LEVELS.get());
        final List<UpgradeLevel> levels_ = levels.stream().map(lvl -> UpgradeLevel.deserialize(lvl)).collect(Collectors.toList());
        return new UpgradeItem(slot, amount, levels_);
    }

    public int getSlot() {
        return slot;
    }

    public int getAmount() {
        return amount;
    }

    public List<UpgradeLevel> getLevels() {
        return levels;
    }
}
