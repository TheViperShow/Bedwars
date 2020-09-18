package me.thevipershow.aussiebedwars.game;

import java.util.Iterator;
import java.util.LinkedList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class SwordUpgrades {

    private final LinkedList<ItemStack> levels;

    public SwordUpgrades() {
        this.levels = new LinkedList<>();
        fillDefaultLevels();
    }

    private void fillDefaultLevels() {
        if (levels.isEmpty()) {
            levels.offerLast(new ItemStack(Material.WOOD_SWORD, 1));
            levels.offerLast(new ItemStack(Material.STONE_SWORD, 1));
            levels.offerLast(new ItemStack(Material.IRON_SWORD, 1));
            levels.offerLast(new ItemStack(Material.DIAMOND_SWORD, 1));
        }
    }

    public final LinkedList<ItemStack> getLevels() {
        return levels;
    }

    public final ItemStack getPrevious(final Material swordType) {
        Iterator<ItemStack> i = levels.descendingIterator();
        while (i.hasNext()) {
            final ItemStack current = i.next();
            if (current.getType() == swordType) {
                if (i.hasNext()) {
                    return i.next().clone();
                }
                break;
            }
        }
        return null;
    }
}
