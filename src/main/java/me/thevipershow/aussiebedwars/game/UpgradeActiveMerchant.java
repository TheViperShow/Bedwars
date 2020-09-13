package me.thevipershow.aussiebedwars.game;

import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.Merchant;

public final class UpgradeActiveMerchant extends AbstractActiveMerchant {

    public UpgradeActiveMerchant(final ActiveGame activeGame, final Merchant merchant, final BedwarsTeam team) {
        super(activeGame, merchant, team);
    }
}
