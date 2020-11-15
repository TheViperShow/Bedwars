package me.thevipershow.bedwars.game.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.UpgradeItem;
import me.thevipershow.bedwars.config.objects.UpgradeLevel;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeType;
import me.thevipershow.bedwars.game.GameUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class BedwarsPlayer {

    private BedwarsPlayer(Player player) {
        this.player = player;
    }

    public static BedwarsPlayer from(Player player) {
        return new BedwarsPlayer(player);
    }

    /*------------------------------------------------------------------------------------*/

    private final Player player;
    private final Map<UpgradeItem, Integer> upgradeItemLevelMap = new HashMap<>();
    private boolean hidden = false;
    private boolean immuneToTraps = false;
    private PlayerState playerState = PlayerState.NONE;
    private BedwarsTeam bedwarsTeam = null;

    /*------------------------------------------------------------------------------------*/

    /**
     * Upgrade someone's item and give the next level to him.
     *
     * @param upgradeItem The UpgradeItem to be upgraded.
     * @return true if he upgraded, false if he could not upgrade.
     */
    public final boolean upgradeAndGiveItem(UpgradeItem upgradeItem) {
        if (!player.isOnline()) return false;
        int currentLvl = getUpgradeItemLevel(upgradeItem);
        List<UpgradeLevel> lvls = upgradeItem.getLevels();
        if (currentLvl + 2 <= lvls.size()) {
            UpgradeLevel toGive = lvls.get(currentLvl + 1);
            GameUtils.giveStackToPlayer(toGive.generateGameStack(), this.player, this.player.getInventory().getContents());
            this.increaseUpgradeItemLevel(upgradeItem);
            return true;
        }
        return false;
    }

    public final void increaseUpgradeItemLevel(UpgradeItem upgradeItem) {
        if (this.upgradeItemLevelMap.containsKey(upgradeItem)) {
            this.upgradeItemLevelMap.compute(upgradeItem, (k, v) -> v += 1);
        } else {
            this.upgradeItemLevelMap.put(upgradeItem, -1);
        }
    }

    public final int getUpgradeItemLevel(UpgradeItem upgradeItem) {
        final Integer i = this.upgradeItemLevelMap.get(upgradeItem);
        if (i != null) {
            return i;
        } else {
            this.upgradeItemLevelMap.put(upgradeItem, +0);
            return +0;
        }
    }

    public final void slideTeleport(char axis, double amount) {
        switch (axis) {
            case 'x':
                teleport(getLocation().add(amount, 0.0, 0.0));
                break;
            case 'y':
                teleport(getLocation().add(0.0, amount, 0.0));
                break;
            case 'z':
                teleport(getLocation().add(0.0, 0.0, amount));
                break;
            default:
                break;
        }
    }

    public final String getName() {
        return player.getName();
    }

    public final BedwarsTeam getBedwarsTeam() {
        return bedwarsTeam;
    }

    public final Map<UpgradeItem, Integer> getUpgradeItemLevelMap() {
        return upgradeItemLevelMap;
    }

    public final void setBedwarsTeam(BedwarsTeam bedwarsTeam) {
        this.bedwarsTeam = bedwarsTeam;
    }

    public final void sendMessage(String s) {
        player.sendMessage(s);
    }

    public final void setImmuneToTraps(boolean immuneToTraps) {
        this.immuneToTraps = immuneToTraps;
    }

    public final boolean isImmuneToTraps() {
        return immuneToTraps;
    }

    public final Inventory getInventory() {
        return player.getInventory();
    }

    public final void playSound(Sound sound, float volume, float pitch) {
        player.playSound(getLocation(), sound, volume, pitch);
    }

    @Deprecated
    public final void sendTitle(final String big, final String small) {
        player.sendTitle(big, small);
    }

    public final boolean isOnline() {
        return player.isOnline();
    }

    public final Location getLocation() {
        return player.getLocation();
    }

    public final void setVelocity(Vector velocity) {
        player.setVelocity(velocity);
    }

    public final Vector getVelocity() {
        return player.getVelocity();
    }

    public final World getWorld() {
        return player.getWorld();
    }

    public final void teleport(Location location) {
        player.teleport(location);
    }

    public final void teleport(Entity destination) {
        player.teleport(destination);
    }

    public final UUID getUniqueId() {
        return player.getUniqueId();
    }

    public final double getHealth() {
        return player.getHealth();
    }

    public final void setHealth(double health) {
        player.setHealth(health);
    }

    public final double getMaxHealth() {
        return player.getMaxHealth();
    }

    public final Player getPlayer() {
        return player;
    }

    public final PlayerState getPlayerState() {
        return playerState;
    }

    public final void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public final boolean isHidden() {
        return hidden;
    }

    public final void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
