package me.thevipershow.bedwars.config.folders.files;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static me.thevipershow.bedwars.AllStrings.BEDS_POS;
import me.thevipershow.bedwars.config.folders.ConfigFiles;
import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;

public final class BedsConfiguration extends AbstractFileConfig {

    private final List<TeamSpawnPosition> bedSpawnPositions;

    public BedsConfiguration(File file) {
        super(file, ConfigFiles.BEDS_FILE);
        bedSpawnPositions = ((List<Map<String, Object>>) getConfiguration().get(BEDS_POS.get())).stream().map(TeamSpawnPosition::deserialize).collect(Collectors.toList());
    }

    public List<TeamSpawnPosition> getBedSpawnPositions() {
        return bedSpawnPositions;
    }
}
