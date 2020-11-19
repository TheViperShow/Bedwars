package me.thevipershow.bedwars.game.managers;

import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.api.GameStartEvent;
import me.thevipershow.bedwars.game.AbstractQueue;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveGameState;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.MatchmakingQueue;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class LobbyManager extends AbstractLobbyManager {

    private final AbstractQueue<Player> associatedQueue;

    public LobbyManager(final ActiveGame activeGame) {
        super(activeGame);
        this.associatedQueue = new MatchmakingQueue(activeGame.getBedwarsGame().getPlayers());
    }

    public void startTicking() {
        this.gameStarterTask = activeGame.getPlugin().getServer().getScheduler().runTaskTimer(activeGame.getPlugin(), () -> {
            if (activeGame.getGameState() == ActiveGameState.STARTED) {
                stopTicking();
            } else if (associatedQueue.queueSize() < activeGame.getBedwarsGame().getMinPlayers()) {
                missingTime = activeGame.getBedwarsGame().getStartTimer();
                final IChatBaseComponent iChat = new ChatMessage(generateMissingPlayerText());
                final PacketPlayOutChat chatPacket = new PacketPlayOutChat(iChat, (byte) 0x2);
                associatedQueue.perform(p -> GameUtils.getPlayerConnection(p).sendPacket(chatPacket));
            } else {
                if (missingTime <= 0) {
                    final GameStartEvent gameStartEvent = new GameStartEvent(activeGame);
                    activeGame.getPlugin().getServer().getPluginManager().callEvent(gameStartEvent);
                    if (gameStartEvent.isCancelled()) {
                        return;
                    }
                    associatedQueue.perform(p -> p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 10.0f, 0.85f));
                    activeGame.start();
                } else {
                    final IChatBaseComponent iChat = new ChatMessage(generateTimeText());
                    final PacketPlayOutChat chatPacket = new PacketPlayOutChat(iChat, (byte) 0x2);
                    associatedQueue.perform(p -> GameUtils.getPlayerConnection(p).sendPacket(chatPacket));

                    if (missingTime > 0 && missingTime <= 5) {
                        associatedQueue.perform(p -> {
                            p.playSound(p.getLocation(), Sound.NOTE_STICKS, 10.0f, 0.850f);
                            p.sendMessage(Bedwars.PREFIX + AllStrings.GAME_STARTING.get() + missingTime);
                        });
                    }

                    missingTime--;
                }

            }
        }, 1L, 20L);
    }

    public final AbstractQueue<Player> getAssociatedQueue() {
        return associatedQueue;
    }
}
