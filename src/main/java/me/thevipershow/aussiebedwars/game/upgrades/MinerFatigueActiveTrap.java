package me.thevipershow.aussiebedwars.game.upgrades;

import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.traps.TrapType;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class MinerFatigueActiveTrap extends ActiveTrap {

    public MinerFatigueActiveTrap(final BedwarsTeam owner, final ActiveGame activeGame) {
        super(owner, TrapType.MINER_FATIGUE, activeGame);
    }

    @Override
    public final void trigger(final Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 15, 1 ,true));
        alertTrapOwners();
    }
}
