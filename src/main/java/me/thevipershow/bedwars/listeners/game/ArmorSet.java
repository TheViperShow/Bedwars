package me.thevipershow.bedwars.listeners.game;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class ArmorSet {

    public static class MapBuilder<K, V> {
        private final Map<K, V> map;

        public MapBuilder(Map<K, V> map) {
            this.map = map;
        }

        public MapBuilder<K, V> put(K k, V v) {
            map.put(k, v);
            return this;
        }

        public Map<K, V> build() {
            return Collections.unmodifiableMap(this.map);
        }
    }

    private final EnumMap<Slots, ItemStack> armorSet = new EnumMap<>(Slots.class);

    public final void upgradeAll(final String type) {
        armorSet.put(Slots.LEGGINGS, Slots.LEGGINGS.generateItemStack(type));
        armorSet.put(Slots.BOOTS, Slots.BOOTS.generateItemStack(type));
    }

    public ArmorSet(final BedwarsTeam team) {
        if (team == null) {
            throw new UnsupportedOperationException();
        }
        for (final Slots slots : Slots.values()) {
            final ItemStack toSet = slots.generateColoredItemStack(team, "LEATHER");
            if (slots == Slots.HELMET) {
                toSet.addEnchantment(Enchantment.WATER_WORKER, 1);
            }
            armorSet.put(slots, toSet);
        }
    }

    public Collection<ItemStack> getItems() {
        return getArmorSet().values();
    }

    public EnumMap<Slots, ItemStack> getArmorSet() {
        return armorSet;
    }
}
