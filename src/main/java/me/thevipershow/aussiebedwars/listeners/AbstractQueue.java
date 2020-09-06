package me.thevipershow.aussiebedwars.listeners;

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
        inQueue.iterator().forEachRemaining(consumer);
    }

    public boolean removeFromQueue(final T t) {
        return inQueue.remove(t);
    }

    public boolean addToQueue(T t) {
        if (isFull()) {
            return inQueue.offerLast(t);
        }
        return false;
    }

    public int queueSize() {
        return this.inQueue.size();
    }

    public boolean isFull() {
        return maximumSize - inQueue.size() > 0;
    }

    public int getMaximumSize() {
        return maximumSize;
    }
}
