package me.thevipershow.aussiebedwars.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;

public abstract class JsonStorage {

    protected final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    protected final File jsonFile;

    public JsonStorage(final File jsonFile) {
        this.jsonFile = jsonFile;
    }

    public abstract void createFile();

    public File getJsonFile() {
        return jsonFile;
    }

    public Gson getGson() {
        return gson;
    }
}
