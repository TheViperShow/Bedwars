package me.thevipershow.aussiebedwars.bedwars;

public enum Gamemode {
    SOLO(1),
    TEAM_2(2),
    TEAM_4(4);

    private final int teamPlayers;

    Gamemode(int teamPlayers) {
        this.teamPlayers = teamPlayers;
    }

    public int getTeamPlayers() {
        return teamPlayers;
    }
}
