package me.thevipershow.aussiebedwars.config;

import java.util.Map;
import java.util.stream.Collectors;
import me.thevipershow.aussiebedwars.LoggerUtils;
import me.thevipershow.aussiebedwars.config.objects.QuadBedwars;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class QuadConfig extends BedwarsGamemodeConfig<QuadBedwars> {

    public QuadConfig(final JavaPlugin plugin) {
        super(plugin, "quad.yml");
        load();
    }

    @Override
    public final void load() {
        bedwarsObjects =
                getConfig().getMapList("quad")
                        .stream()
                        .map(map -> QuadBedwars.deserialize((Map<String, Object>) map))
                        .collect(Collectors.toSet());
    }

    @Override
    public final void reload() {
        bedwarsObjects.clear();
        load();
    }
}
