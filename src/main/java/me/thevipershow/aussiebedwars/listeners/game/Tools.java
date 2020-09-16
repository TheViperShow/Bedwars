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

    private Tools() {
        throw new UnsupportedOperationException("Cannot initialize " + getClass().getName());
    }

    /*
    public enum TYPE {
        SWORD(new ArmorSet.MapBuilder<String, Material>(new HashMap<>())
                .put("WOOD", Material.WOOD_SWORD)
                .put("IRON", Material.IRON_SWORD)
                .put("DIAMOND", Material.DIAMOND_SWORD)
                .build()),
        PICKAXE(new ArmorSet.MapBuilder<String, Material>(new HashMap<>())
                .put("WOOD", Material.WOOD_PICKAXE)
                .put("IRON", Material.IRON_PICKAXE)
                .put("DIAMOND", Material.DIAMOND_PICKAXE)
                .build()),
        AXE(new ArmorSet.MapBuilder<String, Material>(new HashMap<>())
                .put("WOOD", Material.WOOD_AXE)
                .put("IRON", Material.IRON_AXE)
                .put("DIAMOND", Material.DIAMOND_AXE)
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

    public final EnumMap<TYPE, ItemStack> getToolsMap() {
        return toolsMap;
    }

    public Collection<ItemStack> getItems() {
        return getToolsMap().values();
    }

    public final void giveToPlayer(final Player p) {
        getItems().forEach(i -> GameUtils.giveStackToPlayer(i, p, p.getInventory().getContents()));
    }

    public final void upgradeTool(final Tools.TYPE type) {
        final Material current = this.toolsMap.get(type).getType();
        if (current == null) return;
        final String startsWith = current.name().split("_")[0];
        switch (startsWith) {
            case "WOOD":
                this.toolsMap.put(type, type.getItem("IRON"));
                break;
            case "IRON":
                this.toolsMap.put(type, type.getItem("DIAMOND"));
                break;
            case "DIAMOND":
                break;
        }
    }

    private final EnumMap<TYPE, ItemStack> toolsMap = new EnumMap<>(TYPE.class);

    public Tools() {
        this.toolsMap.put(TYPE.SWORD, TYPE.SWORD.getItem("WOOD"));
    }

     */
}
