package me.thevipershow.bedwars.game.runnables;

import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.config.objects.SpawnPosition;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.game.enums.PlayerState;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;

public final class RespawnRunnable extends BukkitRunnable {

    private int secondsLeft = 0x05;
    private final BedwarsPlayer p;
    private final ActiveGame activeGame;

    public RespawnRunnable(BedwarsPlayer p, ActiveGame activeGame) {
        this.p = p;
        this.activeGame = activeGame;
        p.setPlayerState(PlayerState.RESPAWNING);
        p.getPlayer().setGameMode(GameMode.SPECTATOR);
    }

    /**
     * This static method starts a RespawnRunnable animation for a BedwarsPlayer.
     * @param activeGame The ActiveGame where the BedwarsPlayer is playing on.
     * @param bedwarsPlayer The BedwarsPlayer who will get the animation.
     */
    public static void startForBedwarsPlayer(ActiveGame activeGame, BedwarsPlayer bedwarsPlayer) {
        BukkitRunnable bukkitRunnable = new RespawnRunnable(bedwarsPlayer, activeGame);
        bukkitRunnable.runTaskTimer(activeGame.getPlugin(), 0L, 20L);
    }

    /**
     * This method does the animation.
     */
    @Override
    public final void run() {
        if (p == null || !p.isOnline()) {
            cancel();
            p.setPlayerState(PlayerState.DEAD); // this shouldn't ever be necessary, I'll call it just to be sure.
        } else if (secondsLeft <= 0x00) {
            SpawnPosition spawnPos = activeGame.getCachedGameData().getCachedTeamSpawnPositions().get(p.getBedwarsTeam());
            if (spawnPos != null) {
                p.teleport(spawnPos.toLocation(activeGame.getCachedGameData().getGame())); // teleporting him to his spawn.
                p.getPlayer().setGameMode(GameMode.SURVIVAL);                              // Setting his gamemode to survival
                p.setPlayerState(PlayerState.PLAYING); // setting his state
            }
            cancel();
        } else {
            PlayerConnection conn = GameUtils.getPlayerConnection(p.getPlayer());
            PacketPlayOutTitle emptyTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatMessage(""), 2, 16, 2);
            IChatBaseComponent iChat = new ChatMessage(Bedwars.PREFIX + String.format("§eRespawning in §7%d §es", secondsLeft));
            PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, iChat, 2, 16, 2);
            conn.sendPacket(emptyTitle);
            conn.sendPacket(titlePacket);
            secondsLeft--;
        }
    }
}
