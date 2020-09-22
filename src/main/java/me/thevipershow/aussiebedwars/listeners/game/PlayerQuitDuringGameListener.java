package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.Gamemode;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.GameUtils;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player p = event.getPlayer();
        final World w = p.getWorld();
        if (!w.equals(activeGame.getAssociatedWorld())) return;

        activeGame.getAssociatedWorld().getPlayers().forEach(P -> P.sendMessage(AussieBedwars.PREFIX + "§7" + p.getName() + " §ehas left this game."));
        if (!activeGame.isHasStarted()) {
            activeGame.getAssociatedWorld().getPlayers().forEach(P -> P.sendMessage(AussieBedwars.PREFIX + String.format("§7§l[§e%d§7\\§e%d§7§l]",
                    activeGame.getAssociatedQueue().queueSize(), activeGame.getAssociatedQueue().getMaximumSize())));
        }

        activeGame.removePlayer(p);
        if (activeGame.getBedwarsGame().getGamemode() == Gamemode.SOLO) {
            activeGame.getDestroyedTeams().add(activeGame.getPlayerTeam(p));
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
                activeGame.getPlugin().getServer().getScheduler().runTaskLater(activeGame.getPlugin(), activeGame::stop, 20*15L);
            }
        }
    }
}
