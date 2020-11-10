package me.thevipershow.bedwars.config.folders;

public enum ConfigFiles {
    BEDS_FILE("beds.yml"),
    GENERAL_FILE("general.yml"),
    MERCHANTS_FILE("merchants.yml"),
    SHOP_FILE("shop.yml"),
    SPAWNERS_FILE("spawners.yml"),
    SPAWNS_FILE("spawns.yml"),
    TEAMS_FILE("teams.yml"),
    UPGRADES_FILE("upgrades.yml");

    private final String filename;

    ConfigFiles(String filename) {
        this.filename = filename;
    }

    public final String getFilename() {
        return filename;
    }
}
