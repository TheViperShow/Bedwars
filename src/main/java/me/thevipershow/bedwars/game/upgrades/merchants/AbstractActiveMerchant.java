package me.thevipershow.bedwars.game.upgrades.merchants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.Merchant;
import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.UpgradeLevel;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public abstract class AbstractActiveMerchant {

    protected final ActiveGame activeGame;
    protected final Merchant merchant;
    protected final BedwarsTeam team;

    protected Villager villager = null;

    public AbstractActiveMerchant(ActiveGame activeGame, Merchant merchant, BedwarsTeam team) {
        this.activeGame = activeGame;
        this.merchant = merchant;
        this.team = team;
    }

    public static List<String> priceDescriptorSection(int price, Material buyWith) {
        return Collections.unmodifiableList(Arrays.asList(
                "",
                AllStrings.GENERATE_PRICE_LORE.get() + price,
                AllStrings.GENERATE_BUY_LORE.get() + GameUtils.beautifyCaps(buyWith.name())
        ));
    }

    public static List<String> priceDescriptorSection(ShopItem i) {
        return priceDescriptorSection(i.getBuyCost(), i.getBuyWith());
    }

    public static List<String> priceDescriptorSection(UpgradeLevel level) {
        return priceDescriptorSection(level.getPrice(), level.getBuyWith());
    }

    public final boolean isActive() {
        return villager != null;
    }

    public final void setupVillager() {
        if (isActive()) {
            return;
        }

        Location spawnAt = merchant.getMerchantPosition().toLocation(activeGame.getCachedGameData().getGame());
        spawnAt.getWorld().loadChunk(spawnAt.getChunk());
        this.villager = (Villager) activeGame.getCachedGameData().getGame().spawnEntity(spawnAt, EntityType.VILLAGER);
        villager.setCustomName(this.merchant.getMerchantName());
        villager.setCustomNameVisible(true);
        villager.setCanPickupItems(false);
        // GameUtils.setAI(this.villager, false);
    }

    public final void spawn() {
        setupVillager();
    }

    public final void delete() {
        if (isActive()) {
            this.villager.remove();
        }
    }

    public final Merchant getMerchant() {
        return merchant;
    }

    public final Villager getVillager() {
        return villager;
    }
}
