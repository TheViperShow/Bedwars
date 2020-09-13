package me.thevipershow.aussiebedwars.listeners.game;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import me.thevipershow.aussiebedwars.game.GameUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class Tools {

    public enum TYPE {
        SWORD(new ArmorSet.MapBuilder<String, Material>(new HashMap<>())
                .put("WOOD", Material.WOOD_SWORD)
                .put("STONE", Material.STONE_SWORD)
                .put("IRON", Material.IRON_SWORD)
                .put("DIAMOND", Material.DIAMOND_SWORD)
                .build()),
        PICKAXE(new ArmorSet.MapBuilder<String, Material>(new HashMap<>())
                .put("WOOD", Material.WOOD_PICKAXE)
                .put("STONE", Material.STONE_PICKAXE)
                .put("IRON", Material.IRON_PICKAXE)
                .put("DIAMOND", Material.DIAMOND_PICKAXE)
                .build());

        private final Map<String, Material> map;

        TYPE(Map<String, Material> map) {
            this.map = map;
        }

        public Map<String, Material> getMap() {
            return map;
        }

        public ItemStack getItem(final String type) {
            return new ItemStack(map.get(type));
        }
    }

    public EnumMap<TYPE, ItemStack> getToolsMap() {
        return toolsMap;
    }

    public Collection<ItemStack> getItems() {
        return getToolsMap().values();
    }

    public void giveToPlayer(final Player p) {
        getItems().forEach(i -> GameUtils.giveStackToPlayer(i, p, p.getInventory().getContents()));
    }

    private final EnumMap<TYPE, ItemStack> toolsMap = new EnumMap<>(TYPE.class);

    public Tools() {
        this.toolsMap.put(TYPE.SWORD, TYPE.SWORD.getItem("WOOD"));
    }
}
