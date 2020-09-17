package me.thevipershow.aussiebedwars.config.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        throw new UnsupportedOperationException("no");
    }

    public static UpgradeItem deserialize(final Map<String, Object> map) {
        final int slot = (int) map.get("slot");
        final int amount = (int) map.get("amount");
        final List<Map<String, Object>> levels = (List<Map<String, Object>>) map.get("levels");
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
