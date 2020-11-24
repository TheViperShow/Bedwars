package me.thevipershow.bedwars.listeners.unregisterable;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.data.game.BedwarsPlayer;
import me.thevipershow.bedwars.game.data.game.enums.PlayerState;
import me.thevipershow.bedwars.game.managers.MovementsManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class CompassUtilizeUnregisterableListener extends UnregisterableListener {

    public CompassUtilizeUnregisterableListener(ActiveGame activeGame) {
        super(activeGame);
    }

    @EventHandler(priority = EventPriority.LOW)
    public final void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().equals(activeGame.getCachedGameData().getGame())) {
            return;
        }

        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand == null) {
            return;
        }

        Material materialInHand = itemInHand.getType();
        if (materialInHand != Material.COMPASS) {
            return;
        }

        BedwarsPlayer bedwarsPlayer = activeGame.getPlayerMapper().get(player);
        if (bedwarsPlayer == null) {
            return;
        }

        if (bedwarsPlayer.getPlayerState() != PlayerState.DEAD) {
            return;
        }

        Action clickType = event.getAction();
        if (clickType != Action.RIGHT_CLICK_BLOCK && clickType != Action.RIGHT_CLICK_AIR) {
            return;
        }

        MovementsManager movementsManager = activeGame.getMovementsManager();
        movementsManager.moveToSpawn(player);
        bedwarsPlayer.playSound(Sound.ENDERMAN_TELEPORT, 9.0f, 0.8f);
    }
}
