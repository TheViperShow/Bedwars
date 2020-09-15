package me.thevipershow.aussiebedwars.config;

import java.util.Map;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.config.objects.DuoBedwars;
import org.bukkit.plugin.java.JavaPlugin;

public final class DuoConfig extends BedwarsGamemodeConfig<DuoBedwars> {
    public DuoConfig(JavaPlugin plugin) {
        super(plugin, "duo.yml");
        load();
    }

    @Override
    public void load() {
        bedwarsObjects =
                getConfig().getMapList("duo")
                        .stream()
                        .map(map -> DuoBedwars.deserialize((Map<String, Object>) map))
                        .collect(Collectors.toSet());
    }

    @Override
    public void reload() {
        bedwarsObjects.clear();
        load();
    }
}
