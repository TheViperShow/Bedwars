package me.thevipershow.aussiebedwars.game.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.thevipershow.aussiebedwars.AussieBedwars;
import me.thevipershow.aussiebedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.game.AbstractActiveMerchant;
import me.thevipershow.aussiebedwars.game.ActiveGame;
import me.thevipershow.aussiebedwars.game.ActiveSpawner;
import me.thevipershow.aussiebedwars.listeners.UnregisterableListener;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class SoloActiveGame extends ActiveGame {

    public SoloActiveGame(String associatedWorldFilename, BedwarsGame bedwarsGame, World lobbyWorld, Plugin plugin) {
        super(associatedWorldFilename, bedwarsGame, lobbyWorld, plugin);
    }

    @Override
    public void start() {
        setHasStarted(true);
        if (associatedQueue.queueSize() >= bedwarsGame.getMinGames()) {
            if (associatedWorld == null) {
                handleError("Something went wrong while you were being sent into the game.");
                return;
            }
            assignTeams(); // putting each player in a different team in the map .
            assignScoreboards(); // starting and assiging a scoreboard for each player. //TODO: FINISH (DONE) - CHECK
            createSpawners(); // creating and spawning ore spawners for this map. //TODO: FINISH
            createMerchants(); // creating and spawning merchants for this map. //TODO: FINISH
            moveTeamsToSpawns(); // moving everyone to their team's spawn.
        }
    }

    @Override
    public void moveTeamsToSpawns() {
        super.assignedTeams.forEach((k, v) -> {
            final Player p = v.stream().findAny().get();
            super.bedwarsGame.getMapSpawns()
                    .stream()
                    .filter(pos -> pos.getBedwarsTeam() == k)
                    .findAny()
                    .ifPresent(spawnPos -> {
                        p.teleport(spawnPos.toLocation(associatedWorld));
                        p.setGameMode(GameMode.SURVIVAL);
                    });
        });
    }

    @Override
    public void stop() {
        moveAllToLobby();
        this.getActiveSpawners().forEach(ActiveSpawner::despawn);
        this.getActiveSpawners().clear();
        this.getActiveMerchants().forEach(AbstractActiveMerchant::delete);
        this.getActiveMerchants().clear();
        this.getAssignedTeams().clear();
        this.getActiveScoreboards().forEach(Scoreboard::deactivate);
        this.getActiveScoreboards().clear();
        this.getDestroyedTeams().clear();
        this.getUnregisterableListeners().forEach(UnregisterableListener::unregister);
        this.getUnregisterableListeners().clear();
        this.associatedQueue.cleanQueue();
        setHasStarted(false);
        destroyMap();
    }

    @Override
    public void declareWinner(final BedwarsTeam team) {
        if (winnerDeclared) return;
        associatedQueue.perform(p -> {
            if (p.isOnline() && p.getWorld().equals(associatedWorld)) {
                p.sendTitle("§7Team " + '§' + team.getColorCode() + team.name() + " §7has won the game!", "§7Returning to lobby in 15s");
            }
        });
        this.winnerDeclared = true;
    }

    @Override
    public void assignTeams() {
        final Iterator<BedwarsTeam> loadedTeams = bedwarsGame.getTeams().iterator();
        associatedQueue.perform(p -> {
            if (loadedTeams.hasNext()) {
                super.assignedTeams.put(loadedTeams.next(), Collections.singleton(p));
            }
        });
    }

    @Override
    public void assignScoreboards() {
        for (Map.Entry<BedwarsTeam, Set<Player>> entry : assignedTeams.entrySet()) {
            final Player p = entry.getValue().stream().findAny().get();
            final Scoreboard scoreboard = ScoreboardLib.createScoreboard(p);

            scoreboard.setHandler(new ScoreboardHandler() {

                @Override
                public String getTitle(final Player player) {
                    return AussieBedwars.PREFIX;
                }

                @Override
                public List<Entry> getEntries(final Player player) {
                    final EntryBuilder builder = new EntryBuilder();
                    for (BedwarsTeam t : assignedTeams.keySet()) {
                        builder.next(" §7Team " + "§" + t.getColorCode() + t.name() + getTeamChar(t));
                    }
                    return builder.build();
                }

            }).setUpdateInterval(20L);

            super.activeScoreboards.add(scoreboard);
            scoreboard.activate();
        }
    }

    @Override
    public void destroyTeamBed(final BedwarsTeam team) {
        associatedQueue.perform(p -> {
            if (p.isOnline() && p.getWorld().equals(associatedWorld)) {
                if (getPlayerTeam(p) == team) {
                    p.sendTitle("§e§lYour bed has been broken!", "");
                    p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 10.0f, 1.0f);
                } else {
                    p.sendMessage("§" + team.getColorCode() + team.name() + " §7team's bed has been broken!");
                }
            }
        });
    }
}
