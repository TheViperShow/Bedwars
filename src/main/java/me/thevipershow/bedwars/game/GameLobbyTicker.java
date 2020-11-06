package me.thevipershow.bedwars.game;

import lombok.Getter;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.events.GameStartEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class GameLobbyTicker extends AbstractLobbyTicker {

    @Getter
    private final AbstractQueue<Player> associatedQueue;

    public GameLobbyTicker(final ActiveGame activeGame) {
        super(activeGame);
        this.associatedQueue = new MatchmakingQueue(activeGame.getBedwarsGame().getPlayers());
    }

    public void startTicking() {
        this.gameStarterTask = activeGame.getPlugin().getServer().getScheduler().runTaskTimer(activeGame.getPlugin(), () -> {
            if (activeGame.isHasStarted()) {
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
                    associatedQueue.perform(p -> p.playSound(p.getLocation(), Sound.ANVIL_LAND, 5.0f, 1.0f));
                    activeGame.start();
                } else {
                    final IChatBaseComponent iChat = new ChatMessage(generateTimeText());
                    final PacketPlayOutChat chatPacket = new PacketPlayOutChat(iChat, (byte) 0x2);
                    associatedQueue.perform(p -> GameUtils.getPlayerConnection(p).sendPacket(chatPacket));

                    if (missingTime > 0 && missingTime <= 5) {
                        associatedQueue.perform(p -> {
                            p.playSound(p.getLocation(), Sound.NOTE_STICKS, 9.50f, 0.850f);
                            p.sendMessage(Bedwars.PREFIX + AllStrings.GAME_STARTING.get() + missingTime);
                        });
                    }

                    missingTime -= 1;
                }

            }
        }, 1L, 20L);
    }

}
