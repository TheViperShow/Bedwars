package me.thevipershow.bedwars.listeners.game;

import java.util.Random;
import me.thevipershow.bedwars.events.BedwarsLevelUpEvent;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class LevelUpListener extends UnregisterableListener {

    private final ActiveGame activeGame;
    private final Random rand = new Random(System.currentTimeMillis());

    public LevelUpListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private void sendLevelUpMsg(final BedwarsLevelUpEvent e) {
        final Player player = e.getPlayer();
        player.sendMessage("§7--------------------------------");
        player.sendMessage("            §a♫ §6LEVEL UP! §a♫");
        player.sendMessage("         §eYou are now §3Level " + e.getNewLevel() + "§8!");
        player.sendMessage("§7--------------------------------");
    }

    private void levelUpEffects(final Player player) {
        player.playSound(player.getLocation(), Sound.FIREWORK_TWINKLE, 8.0f, 0.950f);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 8.0f, 0.900f);
        for (int i = 0; i < 15; i++) {
            player.playEffect(player.getLocation().add(rand.nextDouble() % 0.35, rand.nextDouble() % 0.5, rand.nextDouble() % 0.35), Effect.FIREWORKS_SPARK, 0);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBedwarsLevelUp(final BedwarsLevelUpEvent event) {
        sendLevelUpMsg(event);
        levelUpEffects(event.getPlayer());
    }
}
