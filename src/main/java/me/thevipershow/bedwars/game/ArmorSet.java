package me.thevipershow.bedwars.game;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public enum ArmorSet {

    BOOTS(Maps.immutableEntry("leather", Material.LEATHER_BOOTS),
            Maps.immutableEntry("iron", Material.IRON_BOOTS),
            Maps.immutableEntry("diamond", Material.DIAMOND_BOOTS)) {
        @Override
        public final void setArmorPiece(PlayerInventory inventory, ItemStack stack) {
            inventory.setBoots(stack);
        }
    },

    LEGGINGS(Maps.immutableEntry("leather", Material.LEATHER_LEGGINGS),
            Maps.immutableEntry("iron", Material.IRON_LEGGINGS),
            Maps.immutableEntry("diamond", Material.DIAMOND_LEGGINGS)) {
        @Override
        public final void setArmorPiece(PlayerInventory inventory, ItemStack stack) {
            inventory.setLeggings(stack);
        }
    },

    HELMET(Maps.immutableEntry("leather", Material.LEATHER_HELMET),
            Maps.immutableEntry("iron", Material.IRON_HELMET),
            Maps.immutableEntry("diamond", Material.DIAMOND_HELMET)) {
        @Override
        public final void setArmorPiece(PlayerInventory inventory, ItemStack stack) {
            inventory.setHelmet(stack);
        }
    },

    CHESTPLATE(Maps.immutableEntry("leather", Material.LEATHER_CHESTPLATE),
            Maps.immutableEntry("iron", Material.IRON_CHESTPLATE),
            Maps.immutableEntry("diamond", Material.DIAMOND_CHESTPLATE)) {
        @Override
        public final void setArmorPiece(PlayerInventory inventory, ItemStack stack) {
            inventory.setChestplate(stack);
        }
    }
    ;

    private final Entry<String, Material>[] entries;

    public abstract void setArmorPiece(PlayerInventory inventory, ItemStack stack);

    @SafeVarargs
    ArmorSet(Entry<String, Material>... entries) {
        this.entries = entries;
    }

    @SafeVarargs
    public final void setArmor(Player player, String type, final Entry<Enchantment, Integer>... applyToAll) {
        PlayerInventory inv = player.getInventory();
        for (Entry<String, Material> entry : entries) {
            if (entry.getKey().equals(type)) {
                ItemStack stack = new ItemStack(entry.getValue(), 1);
                for (Entry<Enchantment, Integer> enchantmentIntegerEntry : applyToAll) {
                    if (enchantmentIntegerEntry != null) {
                        stack.addUnsafeEnchantment(enchantmentIntegerEntry.getKey(), enchantmentIntegerEntry.getValue());
                    }
                }
                setArmorPiece(inv, stack);
                break;
            }
        }
    }

    final static ArmorSet[] r = {LEGGINGS, BOOTS};

    @SafeVarargs
    public static void setArmorFromType(Player player, String armorRarityType, boolean restricted, final Entry<Enchantment, Integer>... applyToAll) {
        final ArmorSet[] restrictedGroup = !restricted ? values() : r;
        for (ArmorSet armorSet : restrictedGroup) {
            for (Entry<String, Material> entry : armorSet.entries) {
                if (entry.getKey().equals(armorRarityType.toLowerCase(Locale.ROOT))) {
                    armorSet.setArmor(player, armorRarityType, applyToAll);
                    break;
                }
            }
        }
    }
}
