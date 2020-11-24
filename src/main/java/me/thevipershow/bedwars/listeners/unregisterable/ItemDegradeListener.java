package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.game.ActiveGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;

public final class ItemDegradeListener extends UnregisterableListener {

    public ItemDegradeListener(ActiveGame activeGame) {
        super(activeGame);
    }

    @EventHandler(ignoreCancelled = true)
    public final void onPlayerItemConsume(final PlayerItemDamageEvent event) {
        final Player player = event.getPlayer();

        if (player.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            //event.setDamage(0);
            event.setCancelled(true);
        }
    }

}
