package me.thevipershow.bedwars.game;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

public abstract class AbstractQueue<T> {

    private int maximumSize;
    private final LinkedList<T> inQueue = new LinkedList<>();

    public AbstractQueue(final int maximumSize) {
        this.maximumSize = maximumSize;
    }

    public final void cleanQueue() {
        inQueue.clear();
    }

    public final void performAndClean(final Consumer<? super T> consumer) {
        perform(consumer);
        cleanQueue();
    }

    public final void perform(final Consumer<? super T> consumer) {
        inQueue.forEach(consumer);
    }

    public final void removeFromQueue(final T t) {
        inQueue.remove(t);
    }

    public final void addToQueue(T t) {
        if (!isFull()) {
            inQueue.offerLast(t);
        }
    }

    public final boolean isEmpty() {
        return this.inQueue.isEmpty();
    }

    public final boolean contains(final T t) {
        return inQueue.contains(t);
    }

    public final int queueSize() {
        return this.inQueue.size();
    }

    public final boolean isFull() {
        return maximumSize - inQueue.size() < 1;
    }

    public final Iterator<T> getIterator() {
        return inQueue.iterator();
    }

    public final int getMaximumSize() {
        return maximumSize;
    }

    public final LinkedList<T> getInQueue() {
        return inQueue;
    }
}
