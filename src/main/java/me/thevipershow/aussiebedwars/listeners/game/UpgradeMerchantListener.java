package me.thevipershow.aussiebedwars.listeners.game;

import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.UpgradeActiveMerchant;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
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
        if (!(entity instanceof Villager)) return;

        final World w = entity.getWorld();
        if (!w.equals(activeGame.getAssociatedWorld())) return;

        final Player p = event.getPlayer();

        final Villager villager = (Villager) entity;
        UpgradeActiveMerchant upgradeActiveMerchant = activeGame.getTeamUpgradeActiveMerchant(villager);

        if (upgradeActiveMerchant != null) {

            event.setCancelled(true);
            activeGame.openUpgrade(p);
        }
    }
}
