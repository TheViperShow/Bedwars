package me.thevipershow.bedwars.game.objects;

import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@RequiredArgsConstructor(staticName = "from")
@Data
public final class BedwarsPlayer {

    private final Player player;
    private PlayerState playerState = PlayerState.NONE;
    private BedwarsTeam bedwarsTeam = null;

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
}
