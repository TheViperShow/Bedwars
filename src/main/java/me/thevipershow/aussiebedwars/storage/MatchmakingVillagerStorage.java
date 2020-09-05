package me.thevipershow.aussiebedwars.storage;

import java.io.File;
import java.io.IOException;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

public class MatchmakingVillagerStorage extends JsonStorage {

    private final JavaPlugin plugin;

    public MatchmakingVillagerStorage(JavaPlugin plugin, String jsonFileName) {
        super(new File(plugin.getDataFolder(), jsonFileName));
        this.plugin = plugin;
    }

    public final void addVillager(final Villager villager) {

    }

    @Override
    public void createFile() {
        if (!getJsonFile().exists()) {
            try {
                getJsonFile().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
