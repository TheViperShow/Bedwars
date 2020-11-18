package me.thevipershow.bedwars.game.upgrades.traps;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.TrapType;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;

public final class AlarmActiveTrap extends ActiveTrap {

    public AlarmActiveTrap(final BedwarsTeam owner, final ActiveGame activeGame) {
        super(owner, TrapType.ALARM, activeGame);
    }

    @Override
    public final void trigger(final BedwarsPlayer player) {
        activeGame.getInternalGameManager().getInvisibilityManager().showPlayer(player);
        alertTrapOwners();
    }
}
