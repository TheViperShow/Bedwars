package me.thevipershow.bedwars.listeners.game;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ExperienceManager;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class GoodGameListener extends UnregisterableListener {

    private final ActiveGame activeGame;
    private final List<Player> rewarded = new ArrayList<>();
    private static final Pattern spaces = Pattern.compile("\\s+");

    public GoodGameListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {

        final Player p = event.getPlayer();
        if (!rewarded.contains(p) && p.getWorld().equals(activeGame.getAssociatedWorld())) {

            final String[] words = spaces.split(event.getMessage());
            for (final String word : words) {
                if (word.equalsIgnoreCase("gg")) {
                    ExperienceManager.rewardPlayer(10, p, activeGame);
                    rewarded.add(p);
                    break;
                }
            }
        }
    }
}
