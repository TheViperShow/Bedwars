package me.thevipershow.bedwars.game.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.game.GameUtils;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class DuoActiveGame extends ActiveGame {

    public DuoActiveGame(String associatedWorldFilename, BedwarsGame bedwarsGame, World lobbyWorld, Plugin plugin) {
        super(associatedWorldFilename, bedwarsGame, lobbyWorld, plugin);
    }

}
