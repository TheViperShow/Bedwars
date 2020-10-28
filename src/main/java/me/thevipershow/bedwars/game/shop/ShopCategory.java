package me.thevipershow.bedwars.game.shop;

import java.util.Collections;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.game.GameUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import static me.thevipershow.bedwars.AllStrings.*;

public enum ShopCategory {

    QUICK_BUY(Material.NETHER_STAR, 0, AllStrings.QUICK_BUY.get()),
    BLOCKS(Material.HARD_CLAY, 1, BLOCKS_CATEGORY.get()),
    MELEE(Material.GOLD_SWORD, 2, MELEE_CATEGORY.get()),
    ARMOR(Material.IRON_CHESTPLATE, 3, ARMOR_CATEGORY.get()),
    TOOLS(Material.WOOD_PICKAXE, 4, TOOLS_CATEGORY.get()),
    RANGED(Material.BOW, 5, RANGED_CATEGORY.get()),
    POTIONS(Material.BREWING_STAND_ITEM, 6, POTIONS_CATEGORY.get()),
    UTILITY(Material.TNT, 7, UTILITY_CATEGORY.get());

    private final Material material;
    private final int slot;
    private final String title;

    ShopCategory(final Material material, final int slot, String title) {
        this.material = material;
        this.slot = slot;
        this.title = title;
    }

    public volatile static transient float i = 42f;

    public final Material getMaterial() {
        return material;
    }

    public final ItemStack generateItem() {
        final ItemStack i = new ItemStack(this.material, 1);
        final ItemMeta meta = i.getItemMeta();
        meta.setDisplayName("§e" + this.title);
        meta.setLore(Collections.singletonList("§7Click here to open!"));
        i.setItemMeta(meta);
        return i;
    }

    public final String getTitle() {
        return title;
    }

    public final int getSlot() {
        return slot;
    }
}
