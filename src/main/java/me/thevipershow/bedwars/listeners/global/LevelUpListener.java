package me.thevipershow.bedwars.listeners.global;

import java.util.Random;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.api.BedwarsLevelUpEvent;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class LevelUpListener implements Listener {

    private static final Random rand = new Random();

    private static void sendLevelUpMsg(BedwarsLevelUpEvent e) {
        final Player player = e.getPlayer().getPlayer();
        player.sendMessage(String.format(AllStrings.LEVEL_UP.get(), e.getNewLevel()));
    }

    private static void levelUpEffects(Player player) {
        player.playSound(player.getLocation(), Sound.FIREWORK_TWINKLE, 8.0f, 0.950f);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 8.0f, 0.900f);
        for (int i = 0; i < 15; i++) {
            player.playEffect(player.getLocation().add(rand.nextDouble() % 0.35, rand.nextDouble() % 0.5, rand.nextDouble() % 0.35), Effect.FIREWORKS_SPARK, 0);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public final void onBedwarsLevelUp(BedwarsLevelUpEvent event) {
        sendLevelUpMsg(event);
        levelUpEffects(event.getPlayer().getPlayer());
    }
}
