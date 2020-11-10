package me.thevipershow.bedwars.config.folders.files;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import static me.thevipershow.bedwars.AllStrings.TEAMS;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.config.folders.ConfigFiles;

public final class TeamsConfiguration extends AbstractFileConfig {

    private final List<BedwarsTeam> actualTeams;

    public TeamsConfiguration(File file) {
        super(file, ConfigFiles.TEAMS_FILE);
        final List<String> teams = (List<String>) getConfiguration().get(TEAMS.get());
        actualTeams = teams.stream().map(s -> BedwarsTeam.valueOf(s.toUpperCase(Locale.ROOT))).collect(Collectors.toList());
    }

    public List<BedwarsTeam> getActualTeams() {
        return actualTeams;
    }
}
