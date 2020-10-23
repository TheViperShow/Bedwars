package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerQuitDuringGameListener extends UnregisterableListener {
    private final ActiveGame activeGame;

    public PlayerQuitDuringGameListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event) {

        if (!activeGame.isHasStarted()) {
            return;
        }

        final Player p = event.getPlayer();
        final World w = p.getWorld();
        if (!w.equals(activeGame.getAssociatedWorld())) {
            return;
        }

        activeGame.getAssociatedWorld().getPlayers().forEach(P -> P.sendMessage(Bedwars.PREFIX + "§7" + p.getName() + " §ehas left this game."));
        if (!activeGame.isHasStarted()) {
            activeGame.getAssociatedWorld().getPlayers().forEach(P -> P.sendMessage(Bedwars.PREFIX + String.format(" §eStatus §7§l[§a%d§7/§a%d§7§l]",
                    activeGame.getAssociatedQueue().queueSize() - 1, activeGame.getAssociatedQueue().getMaximumSize())));
        }

        activeGame.removePlayer(p);
        final BedwarsTeam pTeam = activeGame.getPlayerTeam(p);
        if (activeGame.getBedwarsGame().getGamemode() == Gamemode.SOLO
                || activeGame.getTeamPlayers(pTeam)
                .stream()
                .filter(P -> P.isOnline() && !activeGame.isOutOfGame(P))
                .count() == 1L) {
            activeGame.getDestroyedTeams().add(pTeam);
        }

        activeGame.getPlayersOutOfGame().add(p);
        GameUtils.clearAllEffects(p);
        GameUtils.clearArmor(p);
        p.getInventory().clear();
        p.setAllowFlight(false);
        p.setFlying(false);

        if (!activeGame.isWinnerDeclared()) {
            final BedwarsTeam bedwarsTeam = activeGame.findWinningTeam();
            if (bedwarsTeam != null) {
                activeGame.declareWinner(bedwarsTeam);
                activeGame.getPlugin().getServer().getScheduler().runTaskLater(activeGame.getPlugin(), activeGame::stop, 20 * 15L);
            }
        }
    }
}
