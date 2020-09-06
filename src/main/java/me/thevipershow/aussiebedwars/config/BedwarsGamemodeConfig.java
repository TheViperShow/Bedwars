package me.thevipershow.aussiebedwars.config;

import java.util.Collections;
import java.util.Set;
import me.thevipershow.aussiebedwars.config.objects.BedwarsGame;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BedwarsGamemodeConfig<T extends BedwarsGame> extends CustomConfigHandler {
    public BedwarsGamemodeConfig(JavaPlugin plugin, String name) {
        super(plugin, name);
    }

    protected Set<T> soloBedwarsObjects = Collections.emptySet();

    public Set<T> getSoloBedwarsObjects() {
        return soloBedwarsObjects;
    }

    public void clear() {
        this.soloBedwarsObjects.clear();
    }

}
