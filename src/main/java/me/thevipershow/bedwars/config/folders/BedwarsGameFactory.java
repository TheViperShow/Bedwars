package me.thevipershow.bedwars.config.folders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.thevipershow.bedwars.config.objects.BedwarsGame;

public final class BedwarsGameFactory {

    private final ValidFoldersDiscoverer validFoldersDiscoverer;

    public BedwarsGameFactory(ValidFoldersDiscoverer validFoldersDiscoverer) {
        this.validFoldersDiscoverer = validFoldersDiscoverer;
    }

    public final List<BedwarsGame> buildGameObjects() {
        List<File> gameFolders = this.validFoldersDiscoverer.validConfigFolders();
        List<BedwarsGame> games = new ArrayList<>(gameFolders.size());

        for (final File gameFolder : gameFolders) {
            final Map<ConfigFiles, File> gameConfigs = ValidFoldersDiscoverer.assignMappings(gameFolder);
            final BedwarsGame bedwarsGame = new BedwarsGame(gameConfigs, gameFolder);
            games.add(bedwarsGame);
        }

        return games;
    }

    public final ValidFoldersDiscoverer getValidFoldersDiscoverer() {
        return validFoldersDiscoverer;
    }
}
