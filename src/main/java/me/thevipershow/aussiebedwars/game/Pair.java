package me.thevipershow.aussiebedwars.game;

public class Pair<A, B> {
    private final A a;
    private final B b;

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }
}
