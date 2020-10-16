package me.thevipershow.bedwars.worlds;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class WorldLoader {
    private final File source;
    private final File destination;

    public WorldLoader(final File source, final File destination) {
        this.source = source;
        this.destination = destination;
    }

    public final File getSource() {
        return source;
    }

    public File getDestination() {
        return destination;
    }

    public boolean copyToDir() {
        try {
            FileUtils.copyDirectory(source, destination);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
