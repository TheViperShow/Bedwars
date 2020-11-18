package me.thevipershow.bedwars.game;

import java.util.HashMap;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public enum Slots {
    HELMET(new ArmorSet.MapBuilder<String, Material>(new HashMap<>())
            .put("LEATHER", Material.LEATHER_HELMET)
            .put("IRON", Material.IRON_HELMET)
            .put("DIAMOND", Material.DIAMOND_HELMET).build()),
    CHESTPLATE(new ArmorSet.MapBuilder<String, Material>(new HashMap<>())
            .put("LEATHER", Material.LEATHER_CHESTPLATE)
            .put("IRON", Material.IRON_CHESTPLATE)
            .put("DIAMOND", Material.DIAMOND_CHESTPLATE).build()),
    LEGGINGS(new ArmorSet.MapBuilder<String, Material>(new HashMap<>())
            .put("LEATHER", Material.LEATHER_LEGGINGS)
            .put("IRON", Material.IRON_LEGGINGS)
            .put("DIAMOND", Material.DIAMOND_LEGGINGS).build()),
    BOOTS(new ArmorSet.MapBuilder<String, Material>(new HashMap<>())
            .put("LEATHER", Material.LEATHER_BOOTS)
            .put("IRON", Material.IRON_BOOTS)
            .put("DIAMOND", Material.DIAMOND_BOOTS).build());

    private final Map<String, Material> bindMap;

    Slots(Map<String, Material> bindMap) {
        this.bindMap = bindMap;
    }

    public Map<String, Material> getBindMap() {
        return bindMap;
    }

    public ItemStack generateItemStack(final String type) {
        return generateItemStack(type, 1);
    }

    public ItemStack generateItemStack(final String type, final int amount) {
        return new ItemStack(this.bindMap.get(type), amount);
    }

    public ItemStack generateColoredItemStack(final BedwarsTeam teamColor, final String type) {
        final ItemStack i = new ItemStack(bindMap.get(type));
        final LeatherArmorMeta colorMeta = (LeatherArmorMeta) i.getItemMeta();
        colorMeta.setColor(teamColor.getRGBColor());
        colorMeta.setDisplayName("§7Team §" + teamColor.getColorCode() + "§l" + teamColor.name());
        i.setItemMeta(colorMeta);
        return i;
    }

    public static void setArmorPiece(final Slots slots, final Player p, final ItemStack stack) {
        final PlayerInventory inv = p.getInventory();
        switch (slots) {
            case HELMET:
                inv.setHelmet(stack);
                break;
            case CHESTPLATE:
                inv.setChestplate(stack);
                break;
            case LEGGINGS:
                inv.setLeggings(stack);
                break;
            case BOOTS:
                inv.setBoots(stack);
                break;
            default:
                break;
        }
    }
}
