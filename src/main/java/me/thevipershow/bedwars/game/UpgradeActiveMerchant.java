package me.thevipershow.bedwars.game;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.Merchant;

public final class UpgradeActiveMerchant extends AbstractActiveMerchant {

    public UpgradeActiveMerchant(final ActiveGame activeGame, final Merchant merchant, final BedwarsTeam team) {
        super(activeGame, merchant, team);
    }
}
