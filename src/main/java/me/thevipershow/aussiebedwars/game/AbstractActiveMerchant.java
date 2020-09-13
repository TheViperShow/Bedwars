package me.thevipershow.aussiebedwars.game;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.config.objects.Merchant;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
    protected final Map<ItemStack, ShopItem> cachedShopItems = new HashMap<>();
    protected final BedwarsTeam team;

    protected Villager villager = null;

    public AbstractActiveMerchant(final ActiveGame activeGame, final Merchant merchant, final BedwarsTeam team) {
        this.activeGame = activeGame;
        this.merchant = merchant;
        this.team = team;
        this.cachedInventory = invFromMerchant(activeGame.getBedwarsGame());
    }

    protected Inventory invFromMerchant(final BedwarsGame bedwarsGame) {
        final Inventory inv = Bukkit.createInventory(null, bedwarsGame.getShop().getSlots(), "§7[§eAussieBedwars§7] §e§lShop");
        for (final int glassSlot : bedwarsGame.getShop().getGlassSlots()) {
            final ItemStack glassStack = new ItemStack(Material.STAINED_GLASS, 1, (short) bedwarsGame.getShop().getGlassColor());
            final ItemMeta glassMeta = glassStack.getItemMeta();
            glassMeta.setDisplayName(" ");
            glassMeta.setLore(Collections.singletonList(" "));
            glassStack.setItemMeta(glassMeta);
            inv.setItem(glassSlot, glassStack);
        }

        for (final ShopItem shopItem : bedwarsGame.getShop().getItems()) {
            ItemStack stack; // = new ItemStack(shopItem.getMaterial(), shopItem.getAmount());
            if (shopItem.getMaterial() == Material.WOOL) {
                stack = new ItemStack(shopItem.getMaterial(), shopItem.getAmount(), team.getWoolColor());
            } else {
                stack = new ItemStack(shopItem.getMaterial(), shopItem.getAmount());
            }
            final ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(shopItem.getItemName());
            meta.setLore(loreFromShopItem(shopItem));
            stack.setItemMeta(meta);
            inv.setItem(shopItem.getSlot(), stack);
            cachedShopItems.put(stack, shopItem);
        }
        return inv;
    }

    protected static List<String> loreFromShopItem(final ShopItem i) {
        return Collections.unmodifiableList(Arrays.asList(
                "§7- §ePrice§7: §6§l" + i.getBuyCost(),
                "§7- §eBuy with§7: §6§l" + GameUtils.beautifyCaps(i.getBuyWith().name())
        ));
    }

    public boolean isActive() {
        return villager != null;
    }

    public void setupVillager() {
        if (isActive()) return;

        final Location spawnAt = merchant.getMerchantPosition().toLocation(activeGame.associatedWorld);
        if (!spawnAt.getWorld().isChunkLoaded(spawnAt.getChunk()))
            spawnAt.getWorld().loadChunk(spawnAt.getChunk());

        this.villager = (Villager) activeGame.associatedWorld.spawnEntity(spawnAt, EntityType.VILLAGER);
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

    public Map<ItemStack, ShopItem> getCachedShopItems() {
        return cachedShopItems;
    }

    public Villager getVillager() {
        return villager;
    }
}
