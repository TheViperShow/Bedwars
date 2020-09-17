package me.thevipershow.aussiebedwars.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.config.objects.Enchantment;
import me.thevipershow.aussiebedwars.config.objects.Merchant;
import me.thevipershow.aussiebedwars.config.objects.ShopItem;
import me.thevipershow.aussiebedwars.config.objects.UpgradeItem;
import me.thevipershow.aussiebedwars.config.objects.UpgradeLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class AbstractActiveMerchant {

    protected final ActiveGame activeGame;
    protected final Merchant merchant;

    protected final BedwarsTeam team;

    protected Villager villager = null;

    public AbstractActiveMerchant(final ActiveGame activeGame, final Merchant merchant, final BedwarsTeam team) {
        this.activeGame = activeGame;
        this.merchant = merchant;
        this.team = team;
    }

    public static List<String> priceDescriptorSection(final ShopItem i) {
        return Collections.unmodifiableList(Arrays.asList(" ",
                "§7- Price§f: §e§l" + i.getBuyCost(),
                "§7- Buy with§f: §e§l" + GameUtils.beautifyCaps(i.getBuyWith().name())
        ));
    }

    public static List<String> priceDescriptorSection(final UpgradeLevel level) {
        return Collections.unmodifiableList(Arrays.asList(" ",
                "§7- Price§f: §e§l" + level.getPrice(),
                "§7- Buy with§f: §e§l" + GameUtils.beautifyCaps(level.getBuyWith().name())
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

    public Villager getVillager() {
        return villager;
    }
}
