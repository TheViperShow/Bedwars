package me.thevipershow.aussiebedwars.listeners.game;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import me.thevipershow.aussiebedwars.game.AbstractActiveMerchant;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameUtils;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public final class GUIInteractListener extends UnregisterableListener {
    private final ActiveGame activeGame;

    public GUIInteractListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private AbstractActiveMerchant merchantOfShop(final Inventory inventory) {
        for (final AbstractActiveMerchant activeMerchant : activeGame.getActiveMerchants())
            if (activeMerchant.getCachedInventory().equals(inventory))
                return activeMerchant;
        return null;
    }

    private boolean makePlayerBuy(final ShopItem shopItem, final Player player, final ItemStack clickedItem) {

        final PlayerInventory playerInventory = player.getInventory();

        final ItemStack[] contents = playerInventory.getContents();
        final HashMap<Integer, Integer> takeFromMap = new HashMap<>();

        int took = 0;
        final int toTake = shopItem.getBuyCost();

        for (int i = 0; i < contents.length; i += 1) {
            if (took >= toTake) break;
            final ItemStack content = contents[i];
            if (content != null && content.getType() == shopItem.getBuyWith()) {
                final int stackAmount = content.getAmount();
                final int leftToTake = toTake - took;
                final int takeFromSlot = Math.min(leftToTake, stackAmount);
                takeFromMap.put(i, takeFromSlot);
                took += takeFromSlot;
            }
        }

        if (took < toTake) { // player does not have enough money, exiting function.
            player.sendMessage(AussieBedwars.PREFIX + "§eYou do not have enough " + GameUtils.beautifyCaps(shopItem.getBuyWith().name()));
            return false;
        }

        // player has money, taking it.
        for (final Map.Entry<Integer, Integer> entry : takeFromMap.entrySet()) {
            final ItemStack item = playerInventory.getItem(entry.getKey());
            item.setAmount(item.getAmount() - entry.getValue());
            playerInventory.setItem(entry.getKey(), item);
        }

        final ItemStack cloned = clickedItem.clone();
        if (cloned.getType() == Material.WOOL) {
            final BedwarsTeam playerTeam = activeGame.getPlayerTeam(player);
            cloned.setDurability(playerTeam.getWoolColor());
            final BedwarsTeam coloredWoolTeam = activeGame.getPlayerTeam(player);
            final ItemMeta clonedMeta = cloned.getItemMeta();
            clonedMeta.setDisplayName("§" + coloredWoolTeam.getColorCode() + "§l" + coloredWoolTeam.name() + " §7Wool");
            clonedMeta.setLore(Collections.singletonList("§7Your team's wool."));
            cloned.setItemMeta(clonedMeta);
        } else if (cloned.getType() == Material.IRON_CHESTPLATE) {
            activeGame.upgradePlayerArmorSet(player, "IRON");
            return true;
        } else if (cloned.getType() == Material.DIAMOND_CHESTPLATE) {
            activeGame.upgradePlayerArmorSet(player, "DIAMOND");
            return true;
        } else {
            final ItemMeta clonedMeta = cloned.getItemMeta();
            clonedMeta.setLore(null);
            cloned.setItemMeta(clonedMeta);
        }

        GameUtils.giveStackToPlayer(cloned, player, contents);
        return true;
    }

    private ShopItem findAssociatedShopItem(final ItemStack clickedItem, final AbstractActiveMerchant abstractActiveMerchant) {
        for (final Map.Entry<ItemStack, ShopItem> entry : abstractActiveMerchant.getCachedShopItems().entrySet()) {
            final boolean similar = entry.getKey().isSimilar(clickedItem);
            if (similar)
                return entry.getValue();
        }
        return null;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(final InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;
        final HumanEntity humanEntity = event.getWhoClicked();
        if (!humanEntity.getWorld().equals(activeGame.getAssociatedWorld())) return;
        if (!(humanEntity instanceof Player)) return;

        event.setCancelled(inventory.getTitle().contains("§7[§eAussieBedwars§7]"));
        final AbstractActiveMerchant abstractActiveMerchant = merchantOfShop(inventory);
        if (abstractActiveMerchant == null) return;
        final ShopItem clickedItem = findAssociatedShopItem(event.getCurrentItem(), abstractActiveMerchant);
        if (clickedItem == null) return;
        switch (abstractActiveMerchant.getMerchant().getMerchantType()) {
            case SHOP: {
                makePlayerBuy(clickedItem, (Player) humanEntity, event.getCurrentItem());
            }
            break;
            case UPGRADE: {

            }
            break;
        }
    }
}
