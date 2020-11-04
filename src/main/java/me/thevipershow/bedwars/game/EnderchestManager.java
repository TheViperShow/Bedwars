package me.thevipershow.bedwars.game;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class EnderchestManager {

    private final ActiveGame activeGame;

    public EnderchestManager(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private final HashMap<UUID, Inventory> enderchestMap = new HashMap<>();

    private Inventory createEnderchest(final Player player) {
        final Inventory inv = activeGame.getPlugin().getServer().createInventory(player, InventoryType.ENDER_CHEST);
        this.enderchestMap.put(player.getUniqueId(), inv);
        return inv;
    }

    public final void openEnderchest(final Player player) {
        Inventory inv = this.enderchestMap.get(player.getUniqueId());
        if (inv == null) {
            inv = createEnderchest(player);
        }
        player.openInventory(inv);
    }

    public final HashMap<UUID, Inventory> getEnderchestMap() {
        return enderchestMap;
    }
}
