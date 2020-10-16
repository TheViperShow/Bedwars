package me.thevipershow.bedwars.config.objects.upgradeshop;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class IronForgeUpgrade implements ConfigurationSerializable , Upgrade {

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("no");
    }

    private final List<UpgradeShopItem> levels;
    private final int slot;

    public IronForgeUpgrade(List<UpgradeShopItem> levels, int slot) {
        this.levels = levels;
        this.slot = slot;
    }

    public static IronForgeUpgrade deserialize(final Map<String, Object> map) {
        final int slot = (int) map.get("slot");
        return new IronForgeUpgrade(
                ((List<Map<String, Object>>) map.get("levels"))
                        .stream().map(UpgradeShopItem::deserialize)
                        .collect(Collectors.toList()),
                slot);
    }

    public int getSlot() {
        return slot;
    }

    public List<UpgradeShopItem> getLevels() {
        return levels;
    }

    @Override
    public final UpgradeType getType() {
        return UpgradeType.IRON_FORGE;
    }
}
