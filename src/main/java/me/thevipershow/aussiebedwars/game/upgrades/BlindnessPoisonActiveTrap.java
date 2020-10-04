package me.thevipershow.aussiebedwars.game.upgrades;

import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.traps.TrapType;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class BlindnessPoisonActiveTrap extends ActiveTrap {
    public BlindnessPoisonActiveTrap(BedwarsTeam owner, ActiveGame activeGame) {
        super(owner, TrapType.BLINDNESS_AND_POISON, activeGame);
    }

    @Override
    public final void trigger(final Player player) {
        final PotionEffect poison = new PotionEffect(PotionEffectType.POISON, 10 , 1);
        final PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 5, 1);

        player.addPotionEffect(poison);
        player.addPotionEffect(blindness);

        alertTrapOwners();
    }
}
