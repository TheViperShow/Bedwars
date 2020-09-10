package me.thevipershow.aussiebedwars.game;

import java.util.LinkedList;
import java.util.function.Consumer;

public abstract class AbstractQueue<T> {

    private int maximumSize;
    private final LinkedList<T> inQueue = new LinkedList<>();

    public AbstractQueue(final int maximumSize) {
        this.maximumSize = maximumSize;
    }

    public void cleanQueue() {
        inQueue.clear();
    }

    public void performAndClean(final Consumer<? super T> consumer) {
        perform(consumer);
        cleanQueue();
    }

    public void perform(final Consumer<? super T> consumer) {
        inQueue.forEach(consumer);
    }

    public boolean removeFromQueue(final T t) {
        return inQueue.remove(t);
    }

    public boolean addToQueue(T t) {
        if (!isFull()) {
            inQueue.offerLast(t);
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return this.inQueue.isEmpty();
    }

    public boolean contains(final T t) {
        return inQueue.contains(t);
    }

    public int queueSize() {
        return this.inQueue.size();
    }

    public boolean isFull() {
        return maximumSize - inQueue.size() < 1;
    }

    public int getMaximumSize() {
        return maximumSize;
    }
}
