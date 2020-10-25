package me.thevipershow.bedwars.config;

import java.util.Collections;
import java.util.Set;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.LoggerUtils;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BedwarsGamemodeConfig<T extends BedwarsGame> extends CustomConfigHandler {

    public BedwarsGamemodeConfig(JavaPlugin plugin, String name) {
        super(plugin, name);
        LoggerUtils.logColor(plugin.getLogger(), AllStrings.LOADING_YML.get() + getClass().getSimpleName());
    }

    protected Set<T> bedwarsObjects = Collections.emptySet();

    public Set<T> getBedwarsObjects() {
        return bedwarsObjects;
    }

    public void clear() {
        this.bedwarsObjects.clear();
    }

}
