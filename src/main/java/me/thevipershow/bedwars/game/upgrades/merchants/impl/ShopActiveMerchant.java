package me.thevipershow.bedwars.game.upgrades.merchants.impl;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.Merchant;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.upgrades.merchants.AbstractActiveMerchant;

public final class ShopActiveMerchant extends AbstractActiveMerchant {

    public ShopActiveMerchant(final ActiveGame activeGame, final Merchant merchant, final BedwarsTeam team) {
        super(activeGame, merchant, team);
    }

}
