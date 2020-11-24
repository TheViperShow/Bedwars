package me.thevipershow.bedwars.game.data.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Player;

public final class PlayerMapper {

    private final HashMap<UUID, BedwarsPlayer> mappings = new HashMap<>();

    public final void add(Player player) {
        final UUID uuid = player.getUniqueId();
        mappings.putIfAbsent(uuid, BedwarsPlayer.from(player));
    }

    public final void addAll(Collection<Player> players) {
        for (Player player : players) {
            add(player);
        }
    }

    public final HashMap<UUID, BedwarsPlayer> getMappings() {
        return mappings;
    }

    public final BedwarsPlayer get(Player player) {
        return get(player.getUniqueId());
    }

    public final BedwarsPlayer get(UUID uuid) {
        return mappings.get(uuid);
    }
}
