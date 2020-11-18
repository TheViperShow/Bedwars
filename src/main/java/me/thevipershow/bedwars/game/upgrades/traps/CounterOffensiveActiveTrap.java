package me.thevipershow.bedwars.game.upgrades.traps;

import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.TrapType;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.game.enums.PlayerState;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class CounterOffensiveActiveTrap extends ActiveTrap {

    public CounterOffensiveActiveTrap(final BedwarsTeam owner, final ActiveGame activeGame) {
        super(owner, TrapType.COUNTER_OFFENSIVE, activeGame);
    }

    @Override
    public final void trigger(final BedwarsPlayer player) {

        for (TeamData<?> value : activeGame.getTeamManager().getDataMap().values()) {
            for (BedwarsPlayer bedwarsPlayer : value.getAll()) {
                PlayerState state = bedwarsPlayer.getPlayerState();
                if (state != PlayerState.RESPAWNING && state != PlayerState.DEAD) {
                    final PotionEffect strengthEffect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 15 * 20, 1, true);
                    final PotionEffect speedEffect = new PotionEffect(PotionEffectType.SPEED, 15 * 20, 1, true);
                    bedwarsPlayer.getPlayer().addPotionEffect(strengthEffect);
                    bedwarsPlayer.getPlayer().addPotionEffect(speedEffect);
                }
            }
        }

        alertTrapOwners();
    }
}
