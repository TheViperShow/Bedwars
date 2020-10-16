package me.thevipershow.bedwars.game.data;

public enum MovingStatus {
    FORWARDS(+1),
    BACKWARDS(-1);

    private final int value;

    MovingStatus(final int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
