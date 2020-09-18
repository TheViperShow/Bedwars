package me.thevipershow.aussiebedwars.config.objects.upgradeshop;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class ManiacMinerUpgrade implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("no");
    }

    private final List<ShopItem> levels;

    public ManiacMinerUpgrade(List<ShopItem> levels) {
        this.levels = levels;
    }

    public static ManiacMinerUpgrade deserialize(final Map<String, Object> map) {
        return new ManiacMinerUpgrade(
                ((List<Map<String, Object>>) map.get("levels"))
                        .stream().map(ShopItem::deserialize)
                        .collect(Collectors.toList())
        );
    }

    public List<ShopItem> getLevels() {
        return levels;
    }
}
