package me.thevipershow.bedwars.listeners.game;

import java.util.List;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public final class SpawnersMultigiveListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public SpawnersMultigiveListener(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler(ignoreCancelled = true)
    public final void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        if (!activeGame.isHasStarted()) {
            return;
        }
        final Player player = event.getPlayer();
        if (!player.getWorld().equals(activeGame.getAssociatedWorld())) return;

        final BedwarsTeam playerTeam = activeGame.getPlayerTeam(player);

        final ItemStack pickedStack = event.getItem().getItemStack();
        final Material pickedType = pickedStack.getType();
        switch (pickedType) {
            case IRON_INGOT:
            case GOLD_INGOT:
            case DIAMOND:
            case EMERALD: {
                final List<Player> nearby = player.getNearbyEntities(1.50, 1.00, 1.50)
                        .stream()
                        .filter(e -> e instanceof Player && activeGame.getPlayerTeam((Player) e) == playerTeam)
                        .map(e -> (Player) e)
                        .collect(Collectors.toList());
                nearby.forEach(p -> GameUtils.giveStackToPlayer(pickedStack.clone(), p, p.getInventory().getContents()));
            }
            break;
            default:
                break;
        }
    }
}
