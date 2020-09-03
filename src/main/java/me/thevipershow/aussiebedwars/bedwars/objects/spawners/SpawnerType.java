package me.thevipershow.aussiebedwars.bedwars.objects.spawners;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum SpawnerType {

    DIAMOND(new ItemStack(Material.DIAMOND_BLOCK), Material.DIAMOND),
    EMERALD(new ItemStack(Material.EMERALD_BLOCK), Material.EMERALD),
    GOLD(new ItemStack(Material.GOLD_BLOCK), Material.GOLD_INGOT),
    IRON(new ItemStack(Material.IRON_BLOCK), Material.IRON_INGOT);

    private final ItemStack headItem;
    private final Material dropItem;

    SpawnerType(ItemStack headItem, Material dropItem) {
        this.headItem = headItem;
        this.dropItem = dropItem;
    }

    public ItemStack getHeadItem() {
        return headItem;
    }

    public Material getDropItem() {
        return dropItem;
    }
}
