package me.thevipershow.aussiebedwars.config.objects.upgradeshop;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class ManiacMinerUpgrade implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("no");
    }

    private final int slot;
    private final List<UpgradeShopItem> levels;

    public ManiacMinerUpgrade(int slot, List<UpgradeShopItem> levels) {
        this.slot = slot;
        this.levels = levels;
    }

    public static ManiacMinerUpgrade deserialize(final Map<String, Object> map) {
        final int slot = (int) map.get("slot");
        return new ManiacMinerUpgrade(
                slot, ((List<Map<String, Object>>) map.get("levels"))
                        .stream().map(UpgradeShopItem::deserialize)
                        .collect(Collectors.toList())
        );
    }

    public int getSlot() {
        return slot;
    }

    public List<UpgradeShopItem> getLevels() {
        return levels;
    }
}
