package me.thevipershow.aussiebedwars.listeners;

import java.util.UUID;
import me.thevipershow.aussiebedwars.storage.sql.SQLiteDatabase;
import me.thevipershow.aussiebedwars.storage.sql.queue.QueueTableUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class MatchmakingVillagersListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager)) return;
        final UUID uuid = entity.getUniqueId();
        SQLiteDatabase.getConnection().ifPresent(c -> QueueTableUtils.getVillagerGamemode(uuid, c).thenAccept(gamemode -> {
            gamemode.ifPresent(gamemode1 -> {
                //TODO: Do stuff
            });
        }));
    }
}
