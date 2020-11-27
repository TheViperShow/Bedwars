package me.thevipershow.bedwars.game.managers;

import java.util.HashSet;
import java.util.UUID;
import me.thevipershow.bedwars.bedwars.objects.shops.MerchantType;
import me.thevipershow.bedwars.config.objects.Merchant;
import me.thevipershow.bedwars.game.upgrades.merchants.AbstractActiveMerchant;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.upgrades.merchants.impl.ShopActiveMerchant;
import me.thevipershow.bedwars.game.upgrades.merchants.impl.UpgradeActiveMerchant;
import org.bukkit.entity.Villager;

public final class MerchantManager extends AbstractGameManager {

    public MerchantManager(ActiveGame activeGame) {
        super(activeGame);
    }

    private final HashSet<AbstractActiveMerchant> activeMerchants = new HashSet<>();

    public final HashSet<AbstractActiveMerchant> getActiveMerchants() {
        return activeMerchants;
    }

    public final void createAll() {
        for (Merchant merchant : activeGame.getBedwarsGame().getMerchants()) {
            AbstractActiveMerchant abstractActiveMerchant = GameUtils.fromMerchant(merchant, activeGame);
            if (abstractActiveMerchant != null) {
                activeMerchants.add(abstractActiveMerchant);
            }
        }
    }

    public final void spawnAll() {
        for (AbstractActiveMerchant activeMerchant : activeMerchants) {
            activeMerchant.spawn();
        }
    }

    public final void deleteAll() {
        for (AbstractActiveMerchant activeMerchant : activeMerchants) {
            activeMerchant.delete();
        }
    }

    public final ShopActiveMerchant getShopActiveMerchant(Villager villager) {
        UUID uuid = villager.getUniqueId();
        for (AbstractActiveMerchant activeMerchant : this.activeMerchants) {
            if (activeMerchant.getVillager() != null && activeMerchant.getVillager().getUniqueId().equals(uuid) && activeMerchant.getMerchant().getMerchantType() == MerchantType.SHOP) {
                return (ShopActiveMerchant) activeMerchant;
            }
        }
        return null;
    }

    public final UpgradeActiveMerchant getUpgradeActiveMerchant(Villager villager) {
        UUID uuid = villager.getUniqueId();
        for (AbstractActiveMerchant activeMerchant : this.activeMerchants) {
            if (activeMerchant.getVillager() != null && activeMerchant.getVillager().getUniqueId().equals(uuid) && activeMerchant.getMerchant().getMerchantType() == MerchantType.UPGRADE) {
                return (UpgradeActiveMerchant) activeMerchant;
            }
        }
        return null;
    }

}
