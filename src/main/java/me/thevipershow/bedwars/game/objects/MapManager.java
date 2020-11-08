package me.thevipershow.bedwars.game.objects;

import java.io.File;
import java.io.IOException;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.game.ActiveGame;
import me.thevipershow.bedwars.worlds.WorldsManager;
import org.apache.commons.io.FileUtils;

public final class MapManager {

    private final ActiveGame activeGame;

    public MapManager(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    public final void destroyMap() {
        final File wDir = activeGame.getCachedGameData().getGame().getWorldFolder();
        try {
            activeGame.getPlugin().getServer().unloadWorld(activeGame.getCachedGameData().getGame(), false);
            WorldsManager.getInstanceUnsafe().getActiveGameList().remove(this.activeGame);
            FileUtils.deleteDirectory(wDir);
        } catch (IOException e) {
            activeGame.getPlugin().getLogger().severe(AllStrings.DESTROY_MAP_ERROR.get() + activeGame.getBedwarsGame().getMapFilename());
            e.printStackTrace();
        }
    }
}
