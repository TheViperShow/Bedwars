package me.thevipershow.bedwars.bedwars;

public enum Gamemode {
    SOLO(1),
    DUO(2),
    QUAD(4);

    private final int teamPlayers;

    Gamemode(int teamPlayers) {
        this.teamPlayers = teamPlayers;
    }

    public int getTeamPlayers() {
        return teamPlayers;
    }
}
