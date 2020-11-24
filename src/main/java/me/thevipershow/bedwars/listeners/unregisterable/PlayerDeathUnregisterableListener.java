package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.api.BedwarsPlayerDeathEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.game.enums.PlayerState;
import me.thevipershow.bedwars.game.data.teams.TeamData;
import me.thevipershow.bedwars.game.managers.TeamManager;
import me.thevipershow.bedwars.game.data.game.enums.TeamStatus;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public final class PlayerDeathUnregisterableListener extends UnregisterableListener {

    public PlayerDeathUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    private boolean isFinalKill(BedwarsPlayer bedwarsPlayer) {
        return activeGame.getTeamManager().dataOfBedwarsPlayer(bedwarsPlayer).getStatus() == TeamStatus.BED_BROKEN;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    final public void onEntityDamage(EntityDamageEvent event) {
        DamageCause cause = event.getCause();
        Entity damaged = event.getEntity();

        if (damaged.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) damaged;
        BedwarsPlayer bedwarsPlayer = activeGame.getPlayerMapper().get(player);
        boolean isInVoid = player.getLocation().getY() <= 0;
        boolean isKill = player.getHealth() - event.getFinalDamage() <= 0;
        boolean isFinalKill = isFinalKill(bedwarsPlayer);

        if (isKill || isInVoid) {
            final BedwarsPlayerDeathEvent bedwarsPlayerDeathEvent;
            if (!(event instanceof EntityDamageByEntityEvent)) {
                bedwarsPlayerDeathEvent = new BedwarsPlayerDeathEvent(this.activeGame, cause, bedwarsPlayer, null, null, isFinalKill);
            } else {
                Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                if (damager.getType() != EntityType.PLAYER) {
                    bedwarsPlayerDeathEvent = new BedwarsPlayerDeathEvent(this.activeGame, cause, bedwarsPlayer, damager, null, isFinalKill);
                } else {
                    BedwarsPlayer killer = activeGame.getPlayerMapper().get(damager.getUniqueId());
                    bedwarsPlayerDeathEvent = new BedwarsPlayerDeathEvent(this.activeGame, cause, bedwarsPlayer, damager, killer, isFinalKill);
                }
            }
            activeGame.getPlugin().getServer().getPluginManager().callEvent(bedwarsPlayerDeathEvent);

            if (!bedwarsPlayerDeathEvent.isCancelled()) {
                event.setCancelled(true);
                if (isFinalKill) {
                    updatePlayerStateAfterDeath(bedwarsPlayer);
                    activeGame.getTeamManager().checkForTeamLose(bedwarsPlayer.getBedwarsTeam());
                }
            }
        }

    }

    private void updatePlayerStateAfterDeath(final BedwarsPlayer bedwarsPlayer) {
        TeamManager<?> teamManager = activeGame.getTeamManager();
        TeamData<?> dataOfPlayer = teamManager.dataOfBedwarsPlayer(bedwarsPlayer);

        if (dataOfPlayer == null) {
            return;
        }

        if (dataOfPlayer.getStatus() == TeamStatus.BED_BROKEN) {
            bedwarsPlayer.setPlayerState(PlayerState.DEAD);
        }
    }
}
