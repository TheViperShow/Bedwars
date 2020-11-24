package me.thevipershow.bedwars.game;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Map;
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
    };

    private final Entry<String, Material>[] entries;

    public final ItemStack generate(String type) {
        for (Entry<String, Material> entry : entries) {
            if (entry.getKey().equals(type)) {
                return new ItemStack(entry.getValue(), 1);
            }
        }
        return null;
    }

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
                        Enchantment enchantment = enchantmentIntegerEntry.getKey();
                        int level = enchantmentIntegerEntry.getValue();
                        System.out.println("added enchant " + enchantment.getName() + " " + level);
                        stack.addUnsafeEnchantment(enchantment, level);
                    }
                }
                setArmorPiece(inv, stack);
                System.out.println(stack.toString());
                break;
            }
        }
    }

    private final static ArmorSet[] r = {LEGGINGS, BOOTS};

    public static Map<ArmorSet, ItemStack> generateFromType(String type) {
        EnumMap<ArmorSet, ItemStack> enumMap = new EnumMap<>(ArmorSet.class);
        for (final ArmorSet armorSet : values()) {
            for (final Entry<String, Material> entry : armorSet.entries) {
                if (entry.getKey().equals(type)) {
                    enumMap.put(armorSet, new ItemStack(entry.getValue(), 1));
                    break;
                }
            }
        }
        return enumMap;
    }

    public static void setArmorFromType(Player player, String armorRarityType, boolean restricted, Entry<Enchantment, Integer> applyToAll) {
        final ArmorSet[] restrictedGroup = restricted ? r : values();
        PlayerInventory playerInventory = player.getInventory();
        for (int i = 0; i < restrictedGroup.length; i++) {
            ArmorSet armorSet = restrictedGroup[i];
            ItemStack armorStack = armorSet.generate(armorRarityType);
            if (applyToAll != null) {
                armorStack.addUnsafeEnchantment(applyToAll.getKey(), applyToAll.getValue());
            }
            armorSet.setArmorPiece(playerInventory, armorStack);
        }
    }
}
