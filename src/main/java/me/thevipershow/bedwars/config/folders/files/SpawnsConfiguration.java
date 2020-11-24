package me.thevipershow.bedwars.config.folders.files;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static me.thevipershow.bedwars.AllStrings.MAP_SPAWNS;
import me.thevipershow.bedwars.config.folders.ConfigFiles;
import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;

public final class SpawnsConfiguration extends AbstractFileConfig {

    private final Set<TeamSpawnPosition> mapSpawnPos;

    public SpawnsConfiguration(File file) {
        super(file, ConfigFiles.SPAWNS_FILE);
        final List<Map<String, Object>> mapSpawns = (List<Map<String, Object>>) getConfiguration().get(MAP_SPAWNS.get());
        mapSpawnPos = mapSpawns.stream()
                .map(TeamSpawnPosition::deserialize)
                .collect(Collectors.toSet());
    }

    public Set<TeamSpawnPosition> getMapSpawnPos() {
        return mapSpawnPos;
    }
}
