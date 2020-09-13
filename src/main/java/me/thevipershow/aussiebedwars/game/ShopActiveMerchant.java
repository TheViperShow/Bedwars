package me.thevipershow.aussiebedwars.game;

import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.Merchant;

public final class ShopActiveMerchant extends AbstractActiveMerchant {

    public ShopActiveMerchant(final ActiveGame activeGame, final Merchant merchant, final BedwarsTeam team) {
        super(activeGame, merchant, team);
    }

}
