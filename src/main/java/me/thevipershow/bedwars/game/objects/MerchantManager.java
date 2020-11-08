package me.thevipershow.bedwars.game.objects;

import java.util.HashSet;
import me.thevipershow.bedwars.config.objects.Merchant;
import me.thevipershow.bedwars.game.AbstractActiveMerchant;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;

public final class MerchantManager {

    public MerchantManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private final ActiveGame activeGame;
    private final HashSet<AbstractActiveMerchant> activeMerchants = new HashSet<>();

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

    public final ActiveGame getActiveGame() {
        return activeGame;
    }
}
