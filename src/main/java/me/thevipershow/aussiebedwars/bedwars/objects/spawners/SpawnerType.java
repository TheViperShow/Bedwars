package me.thevipershow.aussiebedwars.bedwars.objects.spawners;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum SpawnerType {

    DIAMOND('b', new ItemStack(Material.DIAMOND_BLOCK), Material.DIAMOND),
    EMERALD('a', new ItemStack(Material.EMERALD_BLOCK), Material.EMERALD),
    GOLD('6', new ItemStack(Material.GOLD_BLOCK), Material.GOLD_INGOT),
    IRON('f', new ItemStack(Material.IRON_BLOCK), Material.IRON_INGOT);

    private final char color;
    private final ItemStack headItem;
    private final Material dropItem;

    SpawnerType(char color, ItemStack headItem, Material dropItem) {
        this.color = color;
        this.headItem = headItem;
        this.dropItem = dropItem;
    }

    public char getColor() {
        return color;
    }

    public ItemStack getHeadItem() {
        return headItem;
    }

    public Material getDropItem() {
        return dropItem;
    }
}
