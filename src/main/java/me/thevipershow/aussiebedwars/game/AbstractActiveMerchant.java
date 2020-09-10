package me.thevipershow.aussiebedwars.game;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.config.objects.Merchant;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class AbstractActiveMerchant {

    protected final ActiveGame activeGame;
    protected final Merchant merchant;
    protected final Inventory cachedInventory;
    protected final HashMap<ItemStack, ShopItem> cachedShopItems = new HashMap<>();

    protected Villager villager = null;

    public AbstractActiveMerchant(ActiveGame activeGame, Merchant merchant) {
        this.activeGame = activeGame;
        this.merchant = merchant;
        this.cachedInventory = invFromMerchant(merchant, activeGame.getBedwarsGame());
    }

    protected static Inventory invFromMerchant(final Merchant merchant, final BedwarsGame bedwarsGame) {
        final Inventory inv = Bukkit.createInventory(null, bedwarsGame.getShop().getSlots());
        for (final ShopItem shopItem : bedwarsGame.getShop().getItems()) {
            final ItemStack stacc = new ItemStack(shopItem.getMaterial(), shopItem.getAmount());
            final ItemMeta meta = stacc.getItemMeta();
            meta.setDisplayName(shopItem.getItemName());
            meta.setLore(loreFromShopItem(shopItem));
            stacc.setItemMeta(meta);
            inv.setItem(shopItem.getSlot(), stacc);
        }
        return inv;
    }

    protected static List<String> loreFromShopItem(final ShopItem i) {
        return Collections.unmodifiableList(Arrays.asList(
                "§7- §ePrice§7: §6" + i.getBuyCost() + 'x',
                "§7- §eBuy with§7: §6" + i.getItemName()
                ));
    }

    public boolean isActive() {
        return villager != null;
    }

    public void setupVillager() {
        if (isActive()) return;
        this.villager = (Villager) activeGame.associatedWorld.spawnEntity(merchant.getMerchantPosition().toLocation(activeGame.associatedWorld), EntityType.VILLAGER);
        villager.setCustomNameVisible(true);
        villager.setCustomName(this.merchant.getMerchantName());
        villager.setCanPickupItems(false);
        GameUtils.setAI(this.villager, false);
    }

    public void openUI(final Player p) {
        p.openInventory(cachedInventory);
    }

    public void spawn() {
        setupVillager();
        activeGame.activeMerchants.add(this);
    }

    public void delete() {
        if (isActive())
            this.villager.remove();
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public Inventory getCachedInventory() {
        return cachedInventory;
    }

    public HashMap<ItemStack, ShopItem> getCachedShopItems() {
        return cachedShopItems;
    }

    public Villager getVillager() {
        return villager;
    }
}
