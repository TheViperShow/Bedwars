package me.thevipershow.bedwars.config.folders;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.plugin.Plugin;

public final class ValidFoldersDiscoverer {

    private final Plugin plugin;

    public ValidFoldersDiscoverer(final Plugin plugin) {
        this.plugin = plugin;
    }

    private static boolean containsExactlyFileOnce(Collection<File> files, String filename) {
        return files.stream().filter(s -> s.getName().equals(filename)).count() == 1;
    }

    private static boolean validate(File folder) {
        if (!folder.isDirectory()) {
            return false;
        } else {
            final Collection<String> files = Arrays.stream(folder.listFiles()).map(File::getName).collect(Collectors.toSet());
            for (ConfigFiles value : ConfigFiles.values()) {
                if (!files.contains(value.getFilename())) {
                    return false;
                }
            }
            return true;
        }
    }

    public static Map<ConfigFiles, File> assignMappings(File configFolder) {
        final File[] folderFiles = configFolder.listFiles();
        final Map<ConfigFiles, File> mappings = new EnumMap<>(ConfigFiles.class);
        boolean throwError = false;

        for (ConfigFiles value : ConfigFiles.values()) {
            boolean found = false;
            for (File folderFile : folderFiles) {
                if (value.getFilename().equals(folderFile.getName())) {
                    mappings.put(value, folderFile);
                    found = true;
                }
            }
            if (!found) {
                throwError = true;
                break;
            }
        }
        if (throwError) {
            throw new IllegalArgumentException(String.format("The folder %s did not have all required files to build a BedwarsGame Object!", configFolder.getName()));
        } else {
            return mappings;
        }
    }

    public final List<File> validConfigFolders() {
        final List<File> valid = new ArrayList<>();
        for (final File file : plugin.getDataFolder().listFiles()) {
            if (file.isDirectory() && file.exists()) {
                if (validate(file)) {
                    valid.add(file);
                }
            }
        }
        return valid;
    }

    public final Plugin getPlugin() {
        return plugin;
    }
}
