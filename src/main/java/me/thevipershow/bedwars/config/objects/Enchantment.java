package me.thevipershow.bedwars.config.objects;

import java.util.Map;
import me.thevipershow.bedwars.AllStrings;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class Enchantment implements ConfigurationSerializable {

    private final int level;
    private final org.bukkit.enchantments.Enchantment enchant;

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }

    public static Enchantment deserialize(final Map<String, Object> map) {
        final int level = (int) map.get(AllStrings.LEVEL.get());
        final org.bukkit.enchantments.Enchantment ench = org.bukkit.enchantments.Enchantment.getByName((String) map.get(AllStrings.TYPE.get()));
        return new Enchantment(level, ench);
    }

    public Enchantment(int level, org.bukkit.enchantments.Enchantment enchant) {
        this.level = level;
        this.enchant = enchant;
    }

    public int getLevel() {
        return level;
    }

    public org.bukkit.enchantments.Enchantment getEnchant() {
        return enchant;
    }
}
