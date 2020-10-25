package me.thevipershow.bedwars.game;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.Merchant;
import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.UpgradeLevel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

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

    public static List<String> priceDescriptorSection(final int price, final Material buyWith) {
        return Collections.unmodifiableList(Arrays.asList("",
                AllStrings.GENERATE_PRICE_LORE.get() + price,
                AllStrings.GENERATE_BUY_LORE.get() + GameUtils.beautifyCaps(buyWith.name())
        ));
    }

    public static List<String> priceDescriptorSection(final ShopItem i) {
        return priceDescriptorSection(i.getBuyCost(), i.getBuyWith());
    }

    public static List<String> priceDescriptorSection(final UpgradeLevel level) {
        return priceDescriptorSection(level.getPrice(), level.getBuyWith());
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
