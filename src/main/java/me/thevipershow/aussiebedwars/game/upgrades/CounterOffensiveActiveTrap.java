package me.thevipershow.aussiebedwars.game.upgrades;

import java.util.Collection;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.upgradeshop.traps.TrapType;
import me.thevipershow.aussiebedwars.game.ActiveGame;
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
            final PotionEffect strengthEffect = new PotionEffect(PotionEffectType.HARM, 15, 1, true);
            final PotionEffect speedEffect = new PotionEffect(PotionEffectType.SPEED, 15, 1, true);
            p.addPotionEffect(strengthEffect);
            p.addPotionEffect(speedEffect);
        });

        alertTrapOwners();
    }
}
