package me.thevipershow.bedwars.game.deathmatch.impl;

import java.util.Map;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.deathmatch.AbstractDeathmatch;
import me.thevipershow.bedwars.game.data.game.enums.PlayerState;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import org.bukkit.Sound;

public final class TeamDeathmatch extends AbstractDeathmatch {
    public TeamDeathmatch(final ActiveGame activeGame) {
        super(activeGame);
    }

    @Override
    public void spawnEnderdragons() {
        for (Map.Entry<BedwarsTeam, ? extends TeamData<?>> entry : activeGame.getTeamManager().getDataMap().entrySet()) {
            entry.getValue().perform(bedwarsPlayer -> {
                bedwarsPlayer.sendMessage(Bedwars.PREFIX + AllStrings.DRAGONS_RELEASED.get());
                bedwarsPlayer.playSound(Sound.ENDERDRAGON_GROWL, 8.5f, 1.0f);

                if (bedwarsPlayer.getPlayerState() != PlayerState.DEAD) {
                    spawnDragon(entry.getKey());
                }
            });
        }
    }

    @Override
    public void startDeathMatch() {
        announceDeathmatch();
        activeGame.getPlugin().getServer().getScheduler().runTaskLater(activeGame.getPlugin(), () -> {
            if (isRunning()) {
                spawnEnderdragons();
            }
        }, 600L * 20L);
    }
}
