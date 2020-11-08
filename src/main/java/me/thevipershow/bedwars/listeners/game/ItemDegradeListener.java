package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;

public final class ItemDegradeListener extends UnregisterableListener {

    public ItemDegradeListener(ActiveGame activeGame) {
        super(activeGame);
    }

    /*
    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemConsume(final PlayerItemDamageEvent event) {
        final Player player = event.getPlayer();

        if (!activeGame.isHasStarted()) {
            return;
        }

        if (player.getWorld().equals(activeGame.getAssociatedWorld())) {
            event.setDamage(0);
            event.setCancelled(true);
        }
    }

     */


}
