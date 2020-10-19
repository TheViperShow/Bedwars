package me.thevipershow.bedwars.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import me.thevipershow.bedwars.Bedwars;
import org.bukkit.Bukkit;
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
            killsMap.compute(player.getUniqueId(), (k, v) -> v = (v + 1));
        } else {
            killsMap.put(player.getUniqueId(), 1);
        }
    }

    public final void increaseFinalKillsCounter(final Player player) {
        if (finalKillsMap.containsKey(player.getUniqueId())) {
            finalKillsMap.compute(player.getUniqueId(), (k, v) -> v = (v + 1));
        } else {
            finalKillsMap.put(player.getUniqueId(), 1);
        }
    }

    public final void announceTopThreeScores() {

        final Iterator<Map.Entry<UUID, Integer>> s = new ArrayList<>(killsMap.entrySet()).stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).iterator();

        int count = 0;
        if (s.hasNext()) {
            for (final Player player : activeGame.getAssociatedWorld().getPlayers()) {
                player.sendMessage(Bedwars.PREFIX + " §7Top 3 kill scores:");
                if (s.hasNext() && count < 3) {
                    count++;
                    final Map.Entry<UUID, Integer> next = s.next();
                    player.sendMessage("       §e" + Bukkit.getOfflinePlayer(next.getKey()).getName() + " §7killed §6" + next.getValue() + " §7players.");
                } else {
                    break;
                }
            }
        }
    }

    public final Integer getKills(final UUID uuid) {
        return this.killsMap.get(uuid);
    }

    public final Integer getFinalKills(final UUID uuid) {
        return this.finalKillsMap.get(uuid);
    }
}
