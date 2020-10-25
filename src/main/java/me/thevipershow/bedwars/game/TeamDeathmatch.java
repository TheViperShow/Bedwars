package me.thevipershow.bedwars.game;

import java.util.List;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class TeamDeathmatch extends AbstractDeathmatch {
    public TeamDeathmatch(final ActiveGame activeGame) {
        super(activeGame);
    }

    @Override
    public void spawnEnderdragons() {
        activeGame.getAssociatedQueue().perform(p -> {
            p.sendMessage(Bedwars.PREFIX + AllStrings.DRAGONS_RELEASED.get());
            p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 8.5567f, 1.0f);
        });
        activeGame.getAssignedTeams().forEach((k, v) -> {
            final List<Player> filteredList = v.stream().filter(p -> !activeGame.playersOutOfGame.contains(p)).collect(Collectors.toList());
            if (!filteredList.isEmpty()) {
                spawnDragon(k);
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
