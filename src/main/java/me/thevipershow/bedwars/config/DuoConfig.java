package me.thevipershow.bedwars.config;

import java.util.Map;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.config.objects.DuoBedwars;
import org.bukkit.plugin.java.JavaPlugin;

public final class DuoConfig extends BedwarsGamemodeConfig<DuoBedwars> {

    public DuoConfig(final JavaPlugin plugin) {
        super(plugin, "duo.yml");
        load();
    }

    @Override
    public final void load() {
        bedwarsObjects =
                getConfig().getMapList("duo")
                        .stream()
                        .map(map -> DuoBedwars.deserialize((Map<String, Object>) map))
                        .collect(Collectors.toSet());
    }

    @Override
    public final void reload() {
        bedwarsObjects.clear();
        load();
    }
}
