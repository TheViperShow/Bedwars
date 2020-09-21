package me.thevipershow.aussiebedwars.game;

import java.util.List;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.AussieBedwars;
import org.bukkit.entity.Player;

public final class TeamDeathmatch extends AbstractDeathmatch {
    public TeamDeathmatch(final ActiveGame activeGame) {
        super(activeGame);
    }

    @Override
    public void spawnEnderdragons() {
        activeGame.getAssociatedQueue().perform(p -> p.sendMessage(AussieBedwars.PREFIX + "§6The dragons have been released!"));
        activeGame.getAssignedTeams().forEach((k, v) -> {
            final List<Player> filteredList = v.stream().filter(p -> !activeGame.playersOutOfGame.contains(p)).collect(Collectors.toList());
            if (!filteredList.isEmpty()) {
                spawnDragon(k);
            }
        });

    }

    @Override
    public void announceDeathmatch() {
        activeGame.getAssociatedQueue().perform(p -> {
            p.sendMessage(AussieBedwars.PREFIX + "§6§lDEATHMATCH §r§6Mode is starting soon§7...");
        });
    }

    @Override
    public void startDeathMatch() {
        announceDeathmatch();
        activeGame.plugin.getServer().getScheduler().runTaskLater(activeGame.plugin, () -> {
            if (isRunning()) spawnEnderdragons();
        }, 10L * 20L);
    }
}
