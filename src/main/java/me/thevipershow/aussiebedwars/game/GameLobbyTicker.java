package me.thevipershow.aussiebedwars.game;

import me.thevipershow.aussiebedwars.events.GameStartEvent;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Sound;

public final class GameLobbyTicker extends AbstractLobbyTicker {

    public GameLobbyTicker(final ActiveGame activeGame) {
        super(activeGame);
    }

    public void startTicking() {
        this.gameStarterTask = activeGame.plugin.getServer().getScheduler().runTaskTimer(activeGame.plugin, () -> {
            if (activeGame.hasStarted) {
                stopTicking();
            } else if (activeGame.associatedQueue.queueSize() < activeGame.bedwarsGame.getMinPlayers()) {
                missingTime = activeGame.bedwarsGame.getStartTimer();
                final IChatBaseComponent iChat = new ChatMessage(generateMissingPlayerText());
                final PacketPlayOutChat chatPacket = new PacketPlayOutChat(iChat, (byte) 0x2);
                activeGame.associatedQueue.perform(p -> GameUtils.getPlayerConnection(p).sendPacket(chatPacket));
            } else {
                if (missingTime <= 0) {
                    final GameStartEvent gameStartEvent = new GameStartEvent(activeGame);
                    activeGame.plugin.getServer().getPluginManager().callEvent(gameStartEvent);
                    if (gameStartEvent.isCancelled()) return;
                    activeGame.associatedQueue.perform(p -> p.playSound(p.getLocation(), Sound.ANVIL_LAND, 5.f, 1.f));
                    activeGame.start();
                } else {
                    final IChatBaseComponent iChat = new ChatMessage(generateTimeText());
                    final PacketPlayOutChat chatPacket = new PacketPlayOutChat(iChat, (byte) 0x2);
                    activeGame.associatedQueue.perform(p -> {
                        p.playSound(p.getLocation(), Sound.NOTE_STICKS, 10.f, 1.f);
                        GameUtils.getPlayerConnection(p).sendPacket(chatPacket);
                    });
                    missingTime -= 1;
                }

            }
        }, 1L, 20L);
    }

}
