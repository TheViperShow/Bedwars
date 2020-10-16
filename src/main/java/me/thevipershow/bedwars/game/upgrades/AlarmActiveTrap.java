package me.thevipershow.bedwars.game.upgrades;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.TrapType;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Player;

public final class AlarmActiveTrap extends ActiveTrap {

    public AlarmActiveTrap(final BedwarsTeam owner, final ActiveGame activeGame) {
        super(owner, TrapType.ALARM, activeGame);
    }

    @Override
    public final void trigger(final Player player) {
        activeGame.showPlayer(player);
        alertTrapOwners();
    }
}
