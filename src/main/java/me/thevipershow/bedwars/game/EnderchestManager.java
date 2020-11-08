package me.thevipershow.bedwars.game;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class EnderchestManager {

    private final HashMap<UUID, Inventory> enderchestMap = new HashMap<>();

    private Inventory createEnderchest(Player player) {
        final Inventory inv = Bukkit.createInventory(player, InventoryType.ENDER_CHEST);
        this.enderchestMap.put(player.getUniqueId(), inv);
        return inv;
    }

    public final void openEnderchest(Player player) {
        Inventory inv = this.enderchestMap.get(player.getUniqueId());
        if (inv == null) {
            inv = createEnderchest(player);
        }
        player.openInventory(inv);
    }
}
