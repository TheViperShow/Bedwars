package me.thevipershow.bedwars.commands.tasks;

import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class PlayerEntityLookupResult<T extends Entity> extends AbstractLookupResult<Player, T> {
    public PlayerEntityLookupResult(Player interested) {
        super(interested);
    }

    public abstract List<T> filteredNearbyEntities(double x, double y, double z);
}
