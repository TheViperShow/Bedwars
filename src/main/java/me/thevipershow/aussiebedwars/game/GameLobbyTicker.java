package me.thevipershow.aussiebedwars.game;

import me.thevipershow.aussiebedwars.events.GameStartEvent;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.scheduler.BukkitTask;

public final class GameLobbyTicker {

    private final ActiveGame activeGame;
    private BukkitTask gameStarterTask = null;
    private long missingtime;

    public GameLobbyTicker(final ActiveGame activeGame) {
        this.activeGame = activeGame;
        missingtime = activeGame.bedwarsGame.getStartTimer();
    }

    private String generateTimeText() {
        final StringBuilder strB = new StringBuilder("§eStarting in §7§l[§r");

        byte start = 0x00;
        final long toColor = 0x14 * (activeGame.bedwarsGame.getStartTimer() - missingtime) / activeGame.bedwarsGame.getStartTimer();

        while (start <= 0x14) {
            strB.append('§').append(start > toColor ? 'c' : 'a').append('|');
            start++;
        }
        return strB.append("§7§l] §6").append(missingtime).append(" §eseconds").toString();
    }

    private String generateMissingPlayerText() {
        return "§7[§eAussieBedwars§7]: Missing §e" + (activeGame.bedwarsGame.getMinPlayers() - activeGame.associatedQueue.queueSize()) + " §7more players to play";
    }

    public void startTicking() {
        this.gameStarterTask = activeGame.plugin.getServer().getScheduler().runTaskTimer(activeGame.plugin, () -> {
            if (activeGame.hasStarted) {
                stopTicking();
            } else if (activeGame.associatedQueue.queueSize() < activeGame.bedwarsGame.getMinPlayers()) {
                missingtime = activeGame.bedwarsGame.getStartTimer();
                final IChatBaseComponent iChat = new ChatMessage(generateMissingPlayerText());
                final PacketPlayOutChat chatPacket = new PacketPlayOutChat(iChat, (byte) 0x2);
                activeGame.associatedQueue.perform(p -> GameUtils.getPlayerConnection(p).sendPacket(chatPacket));
            } else {

                if (missingtime <= 0) {
                    final GameStartEvent gameStartEvent = new GameStartEvent(activeGame);
                    activeGame.plugin.getServer().getPluginManager().callEvent(gameStartEvent);
                    if (gameStartEvent.isCancelled()) return;
                    activeGame.start();
                } else {
                    final IChatBaseComponent iChat = new ChatMessage(generateTimeText());
                    final PacketPlayOutChat chatPacket = new PacketPlayOutChat(iChat, (byte) 0x2);
                    activeGame.associatedQueue.perform(p -> GameUtils.getPlayerConnection(p).sendPacket(chatPacket));
                    missingtime -= 1;
                }

            }
        }, 1L, 20L);
    }

    public void stopTicking() {
        this.gameStarterTask.cancel();
    }
}
