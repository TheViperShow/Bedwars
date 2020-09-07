package me.thevipershow.aussiebedwars.game;

import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import me.thevipershow.aussiebedwars.game.impl.SoloActiveGame;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public final class GameUtilities {

    public static ActiveGame fromGamemode(BedwarsGame game,
                                   String associatedWorldFilename,
                                   World lobbyWorld,
                                   Plugin plugin) {
        switch (game.getGamemode()) {
            case DUO:
                return new SoloActiveGame(associatedWorldFilename, game, lobbyWorld, plugin);
            default:
                return null;
        }
    }
}
