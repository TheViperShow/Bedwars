package me.thevipershow.bedwars.game.upgrades;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.TrapType;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class BlindnessPoisonActiveTrap extends ActiveTrap {
    public BlindnessPoisonActiveTrap(BedwarsTeam owner, ActiveGame activeGame) {
        super(owner, TrapType.BLINDNESS_AND_POISON, activeGame);
    }

    @Override
    public final void trigger(final Player player) {
        final PotionEffect poison = PotionEffectType.POISON.createEffect(20 * 10, 1);
        final PotionEffect blindness = PotionEffectType.BLINDNESS.createEffect(5 * 20, 1);

        player.addPotionEffect(poison);
        player.addPotionEffect(blindness);

        alertTrapOwners();
    }
}
