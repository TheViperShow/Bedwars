package me.thevipershow.bedwars.listeners.unregisterable;

import java.util.Objects;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.ActiveGameState;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public final class SpawnersMultigiveUnregisterableListener extends UnregisterableListener {
    public SpawnersMultigiveUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }



    @EventHandler(ignoreCancelled = true)
    public final void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        if (activeGame.getGameState() != ActiveGameState.STARTED) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            return;
        }

        BedwarsPlayer bedwarsPlayer = activeGame.getPlayerMapper().get(player);
        if (bedwarsPlayer == null) {
            return;
        }

        BedwarsTeam playerTeam = bedwarsPlayer.getBedwarsTeam();

        ItemStack pickedStack = event.getItem().getItemStack();

        if (checkForDropItem(pickedStack)) {
            giveSpawnDropToNearby(bedwarsPlayer, pickedStack);
        }

    }

    private boolean checkForDropItem(ItemStack drop) {
        Material dropMaterial = drop.getType();
        return dropMaterial == Material.IRON_INGOT
                || dropMaterial == Material.GOLD_INGOT
                || dropMaterial == Material.DIAMOND
                || dropMaterial == Material.EMERALD;
    }

    private void giveSpawnDropToNearby(BedwarsPlayer player, ItemStack dropped) {
        player.getPlayer().getNearbyEntities(1.5, 1.5, 1.5)
                .stream()
                .filter(e -> e.getType() == EntityType.PLAYER)
                .map(e -> activeGame.getPlayerMapper().get(e.getUniqueId()))
                .filter(Objects::nonNull)
                .filter(bedwarsPlayer -> bedwarsPlayer.getBedwarsTeam() == player.getBedwarsTeam())
                .forEach(bedwarsPlayer -> GameUtils.giveStackToPlayer(dropped.clone(), bedwarsPlayer.getPlayer(), bedwarsPlayer.getInventory().getContents()));
    }


}
