package me.thevipershow.aussiebedwars.game;

import java.util.List;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.SpawnPosition;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderDragon;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
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
                final SpawnPosition teamSpawn = activeGame.getBedwarsGame().spawnPosOfTeam(k);
                final EnderDragon enderDragon = (EnderDragon) activeGame.associatedWorld.spawnEntity(teamSpawn.toLocation(activeGame.getAssociatedWorld()).add(0.0, 30.0, 0.0), EntityType.ENDER_DRAGON);
                enderDragon.setRemoveWhenFarAway(false);
                final CraftEnderDragon coolDragon = ((CraftEnderDragon) enderDragon);
                dragonTargetListener.getDragonPlayerMap().put(coolDragon, filteredList);
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
