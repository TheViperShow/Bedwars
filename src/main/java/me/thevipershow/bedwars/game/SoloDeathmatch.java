package me.thevipershow.bedwars.game;

import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import org.bukkit.Sound;

public final class SoloDeathmatch extends AbstractDeathmatch {

    public SoloDeathmatch(final ActiveGame activeGame) {
        super(activeGame);
    }

    @Override
    public void spawnEnderdragons() {
        activeGame.getAssociatedQueue().perform(p -> {
            p.sendMessage(Bedwars.PREFIX + AllStrings.DRAGONS_RELEASED.get());
            p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 8.5f, 1.0f);
        });
        activeGame.getAssociatedQueue().perform(p -> {
            if (!activeGame.getPlayersOutOfGame().contains(p)) {
                final BedwarsTeam pTeam = activeGame.getPlayerTeam(p);
                if (pTeam != null) {
                    spawnDragon(pTeam);
                }
            }
        });
    }

    @Override
    public void startDeathMatch() {
        announceDeathmatch();
        activeGame.plugin.getServer().getScheduler().runTaskLater(activeGame.plugin, () -> {
            if (isRunning()) {
                spawnEnderdragons();
            }
        }, 600L * 20L);
    }
}
