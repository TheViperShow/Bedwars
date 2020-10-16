package me.thevipershow.bedwars.game.upgrades;

import java.util.Collection;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.TrapType;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class CounterOffensiveActiveTrap extends ActiveTrap {

    public CounterOffensiveActiveTrap(final BedwarsTeam owner, final ActiveGame activeGame) {
        super(owner, TrapType.COUNTER_OFFENSIVE, activeGame);
    }

    @Override
    public final void trigger(final Player player) {
        final Collection<Player> giveEffectTo = activeGame.getTeamPlayers(owner).stream().filter(p -> p.isOnline() && !activeGame.getPlayersRespawning().contains(p) && !activeGame.isOutOfGame(p)).collect(Collectors.toSet());

        giveEffectTo.forEach(p -> {
            final PotionEffect strengthEffect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 15 * 20, 1, true);
            final PotionEffect speedEffect = new PotionEffect(PotionEffectType.SPEED, 15 * 20, 1, true);
            p.addPotionEffect(strengthEffect);
            p.addPotionEffect(speedEffect);
        });

        alertTrapOwners();
    }
}
