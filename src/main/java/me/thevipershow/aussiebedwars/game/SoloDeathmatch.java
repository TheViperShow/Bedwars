package me.thevipershow.aussiebedwars.game;

import java.util.Collections;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.SpawnPosition;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderDragon;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;

public final class SoloDeathmatch extends AbstractDeathmatch {

    public SoloDeathmatch(final ActiveGame activeGame) {
        super(activeGame);
    }

    @Override
    public void spawnEnderdragons() {
        activeGame.getAssociatedQueue().perform(p -> p.sendMessage(AussieBedwars.PREFIX + "§6The dragons have been released!"));
        activeGame.getAssociatedQueue().perform(p -> {
            if (!activeGame.getPlayersOutOfGame().contains(p)) {
                final BedwarsTeam playerTeam = activeGame.getPlayerTeam(p);
                final SpawnPosition teamSpawn = activeGame.getBedwarsGame().spawnPosOfTeam(playerTeam);
                final EnderDragon enderDragon = (EnderDragon) activeGame.associatedWorld.spawnEntity(teamSpawn.toLocation(activeGame.getAssociatedWorld()).add(0.0, 30.0, 0.0), EntityType.ENDER_DRAGON);
                enderDragon.setRemoveWhenFarAway(false);
                final CraftEnderDragon coolDragon = ((CraftEnderDragon) enderDragon);
                dragonTargetListener.getDragonPlayerMap().put(coolDragon, Collections.singletonList(p));
                // todo: upgrade dragon buff
            }
        });
    }

    @Override
    public void announceDeathmatch() {
        activeGame.getAssociatedQueue().perform(p -> p.sendMessage(AussieBedwars.PREFIX + "§6§lDEATHMATCH §r§6Mode is starting soon§7..."));
    }

    @Override
    public void startDeathMatch() {
        announceDeathmatch();
        activeGame.plugin.getServer().getScheduler().runTaskLater(activeGame.plugin, () -> {
            if (isRunning()) spawnEnderdragons();
        }, 10L * 20L);
    }
}
