package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.config.objects.SpawnPosition;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnRunnable extends BukkitRunnable {

    private int secondsLeft = 5;
    private final Player p;
    private final ActiveGame activeGame;

    public RespawnRunnable(final Player p, final ActiveGame activeGame) {
        this.p = p;
        this.activeGame = activeGame;
    }

    @Override
    public final void run() {
        if (!p.isOnline() && p.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            cancel();
        } else if (secondsLeft == 0) {
            final SpawnPosition spawnPos = activeGame.getBedwarsGame().spawnPosOfTeam(activeGame.getPlayerMapper().get(p).getBedwarsTeam());
            if (spawnPos != null) {
                p.teleport(spawnPos.toLocation(activeGame.getCachedGameData().getGame())); // teleporting him to his spawn.
                p.setGameMode(GameMode.SURVIVAL);                                          // Setting his gamemode to survival
            }
            cancel();
        } else {
            final PlayerConnection conn = GameUtils.getPlayerConnection(p);
            final PacketPlayOutTitle emptyTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatMessage(""), 2, 16, 2);
            final IChatBaseComponent iChat = new ChatMessage(Bedwars.PREFIX + String.format("§eRespawning in §7%d §es", secondsLeft));
            final PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, iChat, 2, 16, 2);
            conn.sendPacket(emptyTitle);
            conn.sendPacket(titlePacket);
            secondsLeft--;
        }
    }
}
