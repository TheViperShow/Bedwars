package me.thevipershow.bedwars.game.data;

public final class Pair<A, B> {
    private final A a;
    private final B b;

    public final A getA() {
        return a;
    }

    public final B getB() {
        return b;
    }

    public Pair(final A a, final B b) {
        this.a = a;
        this.b = b;
    }
}
