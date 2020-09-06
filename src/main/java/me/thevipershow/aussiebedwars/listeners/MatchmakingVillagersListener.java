package me.thevipershow.aussiebedwars.listeners;

import java.util.UUID;
import me.thevipershow.aussiebedwars.storage.sql.MySQLDatabase;
import me.thevipershow.aussiebedwars.storage.sql.queue.QueueTableUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.Plugin;

public class MatchmakingVillagersListener implements Listener {

    private final Plugin plugin;

    public MatchmakingVillagersListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager)) return;
        final UUID uuid = entity.getUniqueId();
        MySQLDatabase.getConnection().ifPresent(conn -> QueueTableUtils.getVillagerGamemode(uuid, conn).thenAccept(gamemode -> { // check if connection is available
            gamemode.ifPresent(foundGamemode -> { // we'll find the optimal queue based on "foundGamemode"

            });
        }));
    }
}
