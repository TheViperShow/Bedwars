package me.thevipershow.bedwars.game.data.game;

import java.util.UUID;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.game.data.game.enums.PlayerState;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
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
    private boolean hidden = false;
    private boolean immuneToTraps = false;
    private PlayerState playerState = PlayerState.NONE;
    private BedwarsTeam bedwarsTeam = null;

    /*------------------------------------------------------------------------------------*/

    /*
    public final boolean upgradeAndGiveItem(UpgradeItem upgradeItem, GameInventories gameInventories) {
        if (!player.isOnline()) {
            return false;
        }
        int currentLvl = this.getUpgradeItemLevel(upgradeItem);
        List<UpgradeLevel> lvls = upgradeItem.getLevels();
        if (currentLvl + 2 <= lvls.size()) {

            UpgradeLevel newLevel = lvls.get(currentLvl + 1);
            GameUtils.giveStackToPlayer(newLevel.generateGameStack(), player, getInventory().getContents());

            if (currentLvl + 3 <= lvls.size()) {
                UpgradeLevel updateDisplay = lvls.get(currentLvl + 2);
               // Map<ShopCategory, Inventory> map = gameInventories.getPlayerShop().get(getUniqueId());
                gameInventories.updateItemUpgrade(upgradeItem.getShopCategory(), upgradeItem.getSlot(), updateDisplay, getUniqueId());
                player.updateInventory();
            }

            return true;
        }
        return false;
    }*/

    /*

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
            this.upgradeItemLevelMap.put(upgradeItem, -1);
            return -1;
        }
    }*/

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
/*
    public final Map<UpgradeItem, Integer> getUpgradeItemLevelMap() {
        return upgradeItemLevelMap;
    }*/

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

    public final PlayerInventory getInventory() {
        return player.getInventory();
    }

    public final void playSound(Sound sound, float volume, float pitch) {
        player.playSound(getLocation(), sound, volume, pitch);
    }

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

    @Override
    public String toString() {
        return "BedwarsPlayer{" +
                "uuid=" + getUniqueId() + "," +
                "player=" + player +
                ", playerState=" + playerState +
                ", bedwarsTeam=" + bedwarsTeam +
                '}';
    }
}
