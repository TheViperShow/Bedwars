package me.thevipershow.bedwars.config;

import java.util.Map;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.config.objects.SoloBedwars;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoloConfig extends BedwarsGamemodeConfig<SoloBedwars> {

    public SoloConfig(final JavaPlugin plugin) {
        super(plugin, "solo.yml");
        load();
    }

    @Override
    public void load() {
        bedwarsObjects =
                getConfig().getMapList("solo")
                        .stream()
                        .map(map -> SoloBedwars.deserialize((Map<String, Object>) map))
                        .collect(Collectors.toSet());
    }

    @Override
    public void reload() {
        bedwarsObjects.clear();
        load();
    }
}
