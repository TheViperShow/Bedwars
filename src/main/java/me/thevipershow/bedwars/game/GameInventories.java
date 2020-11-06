package me.thevipershow.bedwars.game;

import java.util.EnumMap;
import lombok.Getter;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.config.objects.Shop;
import me.thevipershow.bedwars.game.shop.ShopCategory;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public class GameInventories {

    private final EnderchestManager enderchestManager;
    private final EnumMap<ShopCategory, Inventory> inventories = new EnumMap<>(ShopCategory.class);

    public GameInventories(final Shop shop) {
        this.enderchestManager = new EnderchestManager();

        for (final ShopCategory shopCategory : ShopCategory.values()) {
            final Inventory inv = Bedwars.plugin.getServer().createInventory(null, 9 * 6, shopCategory.getTitle());
            shop.getGlassSlots().forEach(slot -> {
                final ItemStack glassStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, shopCategory.ordinal() == slot % 9 ? Shop.GREEN_GLASS_DAMAGE : (short) shop.getGlassColor());
                final ItemMeta meta = glassStack.getItemMeta();
                meta.setDisplayName(" ");
                glassStack.setItemMeta(meta);
                inv.setItem(slot, glassStack);
            });
            for (final ShopCategory value : ShopCategory.values()) {
                inv.setItem(value.ordinal(), value.generateItem());
            }
            inventories.put(shopCategory, inv);
        }
        shop.getItems().forEach(item -> inventories.get(item.getShopCategory()).setItem(item.getSlot(), item.getCachedFancyStack()));
        shop.getPotionItem().forEach(item -> inventories.get(item.getShopCategory()).setItem(item.getSlot(), item.getCachedFancyStack()));
        shop.getUpgradeItems().forEach(item -> inventories.get(item.getShopCategory()).setItem(item.getSlot(), item.getLevels().get(0x00).getCachedFancyStack()));
    }

    public final Inventory getFromCategory(final ShopCategory shopCategory) {
        return inventories.get(shopCategory);
    }
}
