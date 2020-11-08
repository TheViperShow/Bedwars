package me.thevipershow.bedwars.game.upgrades;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.TrapType;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.objects.BedwarsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class MinerFatigueActiveTrap extends ActiveTrap {

    public MinerFatigueActiveTrap(final BedwarsTeam owner, final ActiveGame activeGame) {
        super(owner, TrapType.MINER_FATIGUE, activeGame);
    }

    @Override
    public final void trigger(BedwarsPlayer player) {
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 15 * 20, 1 ,true));
        alertTrapOwners();
    }
}
