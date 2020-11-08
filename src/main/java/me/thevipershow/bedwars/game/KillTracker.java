package me.thevipershow.bedwars.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.game.objects.TeamData;
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

        final List<Map.Entry<UUID, Integer>> s = new ArrayList<>(killsMap.entrySet()).stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());
        final List<Map.Entry<UUID, Integer>> s_ = new ArrayList<>(finalKillsMap.entrySet()).stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());

        final List<String> sMessage = s.stream().map(v -> "      §e" + activeGame.getPlugin().getServer().getOfflinePlayer(v.getKey()).getName() + " §7killed §6" + v.getValue() + " §7players.").collect(Collectors.toList());
        final List<String> s_Message = s.stream().map(v -> "      §e" + activeGame.getPlugin().getServer().getOfflinePlayer(v.getKey()).getName() + " §7final killed §6" + v.getValue() + " §7players.").collect(Collectors.toList());

        for (TeamData<?> value : activeGame.getTeamManager().getDataMap().values()) {
            value.perform(bedwarsPlayer -> {
                if (!sMessage.isEmpty()) {
                    sMessage.forEach(bedwarsPlayer::sendMessage);
                }
                if (!s_Message.isEmpty()) {
                    s_Message.forEach(bedwarsPlayer::sendMessage);
                }
            });
        }

        /*
        activeGame.getAssociatedWorld().getPlayers().forEach(p -> {
            if (!this.killsMap.isEmpty()) {
                p.sendMessage(Bedwars.PREFIX + AllStrings.TOP_3_KILL.get());
                final Iterator<Map.Entry<UUID, Integer>> killIterator = s.iterator();
                while (count.getAndIncrement() < 3) {
                    if (killIterator.hasNext()) {
                        final Map.Entry<UUID, Integer> next = killIterator.next();
                        p.sendMessage("      §e" + activeGame.getPlugin().getServer().getOfflinePlayer(next.getKey()).getName() + " §7killed §6" + next.getValue() + " §7players.");
                    } else {
                        break;
                    }
                }
            }

            if (!this.finalKillsMap.isEmpty()) {
                count.set(0x00);
                p.sendMessage(Bedwars.PREFIX + AllStrings.TOP_3_FINAL_KILL.get());
                final Iterator<Map.Entry<UUID, Integer>> finalKillsIterator = s_.iterator();
                while (count.getAndIncrement() < 3) {
                    if (finalKillsIterator.hasNext()) {
                        final Map.Entry<UUID, Integer> next = finalKillsIterator.next();
                        p.sendMessage("      §e" + activeGame.getPlugin().getServer().getOfflinePlayer(next.getKey()).getName() + " §7final killed §6" + next.getValue() + " §7players.");
                    } else {
                        break;
                    }
                }
            }
        });

         */
    }

    public final Integer getKills(final UUID uuid) {
        return this.killsMap.get(uuid);
    }

    public final Integer getFinalKills(final UUID uuid) {
        return this.finalKillsMap.get(uuid);
    }
}
