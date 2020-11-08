package me.thevipershow.bedwars.listeners.game;

import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.UpgradeActiveMerchant;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

@Deprecated
public final class UpgradeMerchantListener extends UnregisterableListener {

    public UpgradeMerchantListener(ActiveGame activeGame) {
        super(activeGame);
    }

    /*
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        final Entity entity = event.getRightClicked();
        if (!activeGame.isHasStarted()) {
            return;
        }

        if (!(entity instanceof Villager)) {
            return;
        }

        final World w = entity.getWorld();
        if (!w.equals(activeGame.getAssociatedWorld())) {
            return;
        }

        final Player p = event.getPlayer();

        final Villager villager = (Villager) entity;
        final UpgradeActiveMerchant upgradeActiveMerchant = activeGame.getTeamUpgradeActiveMerchant(villager);

        if (upgradeActiveMerchant != null) {
            event.setCancelled(true);
            activeGame.openUpgrade(p);
        }
    }

     */
}
