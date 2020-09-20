package me.thevipershow.aussiebedwars.game;

import org.bukkit.scheduler.BukkitTask;

public abstract class AbstractLobbyTicker {

    protected final ActiveGame activeGame;
    protected BukkitTask gameStarterTask = null;
    protected long missingTime;

    protected final String generateTimeText() {
        final StringBuilder strB = new StringBuilder("§eStarting in §7§l[§r");

        byte start = 0x00;
        final long toColor = 0x14 * (activeGame.bedwarsGame.getStartTimer() - missingTime) / activeGame.bedwarsGame.getStartTimer();

        while (start <= 0x14) {
            strB.append('§').append(start > toColor ? 'c' : 'a').append('|');
            start++;
        }
        return strB.append("§7§l] §6").append(missingTime).append(" §eseconds").toString();
    }

    protected final String generateMissingPlayerText() {
        return "§7[§eAussieBedwars§7]: Missing §e" + (activeGame.bedwarsGame.getMinPlayers() - activeGame.associatedQueue.queueSize()) + " §7more players to play";
    }

    public abstract void startTicking();

    public void stopTicking() {
        if (gameStarterTask != null) this.gameStarterTask.cancel();
    }

    public AbstractLobbyTicker(final ActiveGame activeGame) {
        this.activeGame = activeGame;
        this.missingTime = activeGame.bedwarsGame.getStartTimer();
    }
}
