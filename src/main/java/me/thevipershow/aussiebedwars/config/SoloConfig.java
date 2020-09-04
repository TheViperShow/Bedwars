package me.thevipershow.aussiebedwars.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.config.objects.SoloBedwars;
import org.bukkit.plugin.java.JavaPlugin;

public class SoloConfig extends CustomConfigHandler {
    public SoloConfig(final JavaPlugin plugin) {
        super(plugin, "solo.yml");
        load();
    }

    private List<SoloBedwars> soloBedwarsConfigurations = Collections.emptyList();

    @Override
    public void load() {
        soloBedwarsConfigurations =
                getConfig().getMapList("solo")
                        .stream()
                        .map(map -> SoloBedwars.deserialize((Map<String, Object>) map))
                        .collect(Collectors.toList());
    }

    @Override
    public void reload() {
        soloBedwarsConfigurations.clear();
        load();
    }

    public List<SoloBedwars> getSoloBedwarsConfigurations() {
        return soloBedwarsConfigurations;
    }
}
