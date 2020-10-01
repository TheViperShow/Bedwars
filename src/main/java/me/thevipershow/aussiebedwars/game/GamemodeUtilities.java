package me.thevipershow.aussiebedwars.game;

import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.game.impl.DuoActiveGame;
import me.thevipershow.aussiebedwars.game.impl.QuadActiveGame;
import me.thevipershow.aussiebedwars.game.impl.SoloActiveGame;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public final class GamemodeUtilities {

    public static ActiveGame fromGamemode(final String associatedWorldName, final BedwarsGame game, final World lobbyWorld, final Plugin plugin) {
        ActiveGame gameToReturn = null;

        switch (game.getGamemode()) {
            case SOLO:
                gameToReturn = new SoloActiveGame(associatedWorldName, game, lobbyWorld, plugin);
                break;
            case DUO:
                gameToReturn = new DuoActiveGame(associatedWorldName, game, lobbyWorld, plugin);
                break;
            case QUAD:
                gameToReturn = new QuadActiveGame(associatedWorldName, game, lobbyWorld, plugin);
                break;
            default:
                throw new UnsupportedOperationException("Invalid gamemode.");
        }

        return gameToReturn;
    }
}
