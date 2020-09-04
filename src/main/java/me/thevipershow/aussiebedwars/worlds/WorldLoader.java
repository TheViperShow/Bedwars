package me.thevipershow.aussiebedwars.worlds;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.io.FileUtils;

public class WorldLoader {
    private final File source;
    private final String destinationPath;

    public WorldLoader(final File source, final String destinationPath) {
        this.source = source;
        this.destinationPath = destinationPath;
    }

    public final File getSource() {
        return source;
    }

    public final String getDestinationPath() {
        return destinationPath;
    }

    public CompletableFuture<Void> copyToDir() {
        final File newFile = new File(destinationPath);
        newFile.mkdirs();
        try {
            //if (newFile.exists()) newFile.delete(); //TODO: Check if directory overrides.
            newFile.createNewFile();
            return CompletableFuture.runAsync(()->{
                try {
                    FileUtils.copyDirectory(source, newFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
