package me.thevipershow.aussiebedwars.game;

import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import org.bukkit.Sound;

public final class SoloDeathmatch extends AbstractDeathmatch {

    public SoloDeathmatch(final ActiveGame activeGame) {
        super(activeGame);
    }

    @Override
    public void spawnEnderdragons() {
        activeGame.getAssociatedQueue().perform(p -> {
            p.sendMessage(AussieBedwars.PREFIX + "§6§lSUDDEN DEATH §r§6mode has started!");
            p.sendMessage(AussieBedwars.PREFIX + "§6The Ender Dragons have been released!");
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
            if (isRunning()) spawnEnderdragons();
        }, 600L * 20L);
    }
}
