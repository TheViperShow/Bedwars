package me.thevipershow.bedwars.game.managers;

import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbstractLobbyManager {

    protected final ActiveGame activeGame;
    protected BukkitTask gameStarterTask = null;
    protected long missingTime;

    protected final String generateTimeText() {
        final StringBuilder strB = new StringBuilder(AllStrings.STARTING_IN.get());

        byte start = 0x00;
        final long toColor = 0x14 * (activeGame.getBedwarsGame().getStartTimer() - missingTime) / activeGame.getBedwarsGame().getStartTimer();

        while (start <= 0x14) {
            strB.append('§').append(start > toColor ? 'c' : 'a').append('|');
            start++;
        }
        return strB.append(AllStrings.STARTING_END.get()).append(missingTime).append(AllStrings.SECONDS.get()).toString();
    }

    protected final String generateMissingPlayerText() {
        return Bedwars.PREFIX + AllStrings.MISSING.get() + (activeGame.getBedwarsGame().getMinPlayers() - activeGame.getGameLobbyTicker().getAssociatedQueue().queueSize()) + " §7more players to play";
    }

    public abstract void startTicking();

    public void stopTicking() {
        if (gameStarterTask != null) {
            this.gameStarterTask.cancel();
        }
    }

    public AbstractLobbyManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
        this.missingTime = activeGame.getBedwarsGame().getStartTimer();
    }
}
