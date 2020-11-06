package me.thevipershow.bedwars.config.objects.upgradeshop;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.AllStrings;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class ManiacMinerUpgrade implements ConfigurationSerializable , Upgrade {

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
        final int slot = (int) map.get(AllStrings.SLOT.get());
        return new ManiacMinerUpgrade(
                slot, ((List<Map<String, Object>>) map.get(AllStrings.LEVELS.get()))
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

    @Override
    public final UpgradeType getType() {
        return UpgradeType.MANIAC_MINER;
    }
}