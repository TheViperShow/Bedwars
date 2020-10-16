package me.thevipershow.bedwars.game;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Player;

public final class KillTracker {

    private final ActiveGame activeGame;

    public KillTracker(final ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    private final HashMap<UUID, Integer> killsMap = new HashMap<>();
    private final HashMap<UUID, Integer> finalKillsMap = new HashMap<>();

    public final void increaseKillsCounter(final Player player) {
        if (killsMap.containsKey(player.getUniqueId())) {
            killsMap.compute(player.getUniqueId(), (k,v) -> v = (v + 1));
        } else {
            killsMap.put(player.getUniqueId(), 1);
        }
    }

    public final void increaseFinalKillsCounter(final Player player) {
        if (finalKillsMap.containsKey(player.getUniqueId())) {
            finalKillsMap.compute(player.getUniqueId(), (k,v) -> v = (v + 1));
        } else {
            finalKillsMap.put(player.getUniqueId(), 1);
        }
    }

    public final Integer getKills(final UUID uuid) {
        return this.killsMap.get(uuid);
    }

    public final Integer getFinalKills(final UUID uuid) {
        return this.finalKillsMap.get(uuid);
    }
}
