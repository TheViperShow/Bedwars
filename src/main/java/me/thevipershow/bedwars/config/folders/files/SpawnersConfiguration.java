package me.thevipershow.bedwars.config.folders.files;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static me.thevipershow.bedwars.AllStrings.SPAWNERS;
import me.thevipershow.bedwars.config.folders.ConfigFiles;
import me.thevipershow.bedwars.config.objects.Spawner;

public final class SpawnersConfiguration extends AbstractFileConfig {

    private final List<Spawner> spawnerList;

    public SpawnersConfiguration(File file) {
        super(file, ConfigFiles.SPAWNERS_FILE);
        final List<Map<String, Object>> spawners = (List<Map<String, Object>>) getConfiguration().get(SPAWNERS.get());
        spawnerList = spawners.stream().map(Spawner::deserialize).collect(Collectors.toList());
    }

    public List<Spawner> getSpawnerList() {
        return spawnerList;
    }
}
