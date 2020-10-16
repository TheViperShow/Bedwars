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

public final class UpgradeMerchantListener extends UnregisterableListener {

    private final ActiveGame activeGame;

    public UpgradeMerchantListener(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @EventHandler()
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        final Entity entity = event.getRightClicked();
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
            System.out.println("1");

            event.setCancelled(true);
            activeGame.openUpgrade(p);
        }
    }
}
