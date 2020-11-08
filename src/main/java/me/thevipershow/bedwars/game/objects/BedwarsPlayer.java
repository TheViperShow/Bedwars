package me.thevipershow.bedwars.game.objects;

import java.util.UUID;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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

    public final BedwarsTeam getBedwarsTeam() {
        return bedwarsTeam;
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
