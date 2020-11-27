package me.thevipershow.bedwars.listeners.unregisterable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.api.PlayerGoodGameEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.managers.ExperienceManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class GoodGamePointUnregisterableListener extends UnregisterableListener {

    public GoodGamePointUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    private final Set<UUID> spoken = new HashSet<>();

    private static final int GG_EXP_REWARD = 25;
    private void rewardPlayer(BedwarsPlayer bedwarsPlayer) {
        ExperienceManager.rewardPlayer(GG_EXP_REWARD, bedwarsPlayer, activeGame);
        bedwarsPlayer.sendMessage(AllStrings.PREFIX.get() + GameUtils.color("&eYou have gained " + GG_EXP_REWARD + " for saying 'gg' during end game."));
    }

    @EventHandler(ignoreCancelled = true)
    public final void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            return;
        }

        BedwarsPlayer bedwarsPlayer = activeGame.getPlayerMapper().get(player);
        if (bedwarsPlayer == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        String message = event.getMessage().toLowerCase(Locale.ROOT);

        if (spoken.contains(uuid)) {
            return;
        }

        if (message.contains("gg") || message.contains("good game")) {
            PlayerGoodGameEvent playerGoodGameEvent = new PlayerGoodGameEvent(activeGame, bedwarsPlayer, event.getMessage());
            activeGame.callGameEvent(playerGoodGameEvent);

            if (playerGoodGameEvent.isCancelled()) {
                return;
            }

            rewardPlayer(bedwarsPlayer);
            spoken.add(uuid);
        }
    }
}
