package me.thevipershow.aussiebedwars.game;

import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.game.impl.SoloActiveGame;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public final class GameUtilities {

    public static ActiveGame fromGamemode(String associatedWorldName ,BedwarsGame game, World lobbyWorld, Plugin plugin) {
        ActiveGame gameToReturn = null;
        switch (game.getGamemode()) {
            case SOLO:
                gameToReturn = new SoloActiveGame(associatedWorldName, game, lobbyWorld, plugin);
                break;
        }
        // System.out.println("Created a new ActiveGame: " + gameToReturn.toString());
        return gameToReturn;
    }
}
