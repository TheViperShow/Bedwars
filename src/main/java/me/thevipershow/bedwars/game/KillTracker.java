package me.thevipershow.bedwars.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.game.data.teams.TeamData;
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
    }

    public final Integer getKills(final UUID uuid) {
        return this.killsMap.get(uuid);
    }

    public final Integer getFinalKills(final UUID uuid) {
        return this.finalKillsMap.get(uuid);
    }
}
