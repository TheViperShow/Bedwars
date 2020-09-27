package me.thevipershow.aussiebedwars.game.data;

import java.util.LinkedList;
import static me.thevipershow.aussiebedwars.game.data.MovingStatus.FORWARDS;

public final class ImmutableTraversableList<T> {

    public ImmutableTraversableList(final LinkedList<T> list) {
        this.list = list;
    }

    private final LinkedList<T> list;
    private MovingStatus movingStatus = FORWARDS; // always move Forwards on default.
    private int currentIndex = 0x00;

    public T move() {
        switch (movingStatus) {
            case FORWARDS:
                return forward();
            case BACKWARDS:
                return backwards();
            default:
                return null;
        }
    }

    public final boolean hasNext() {
        return (this.list.size()) >= (currentIndex + 1);
    }

    public final void clear() {
        list.clear();
    }

    public final T forward() {
        final int listSize = list.size();
        if (listSize == 0) {
            return null;
        } else if (currentIndex + 1 <= listSize) {
            return list.get(currentIndex++);
        } else {
            currentIndex = 0;
            return list.getFirst();
        }
    }

    public final T backwards() {
        final int listSize = list.size();
        if (listSize == 0) {
            return null;
        } else if (currentIndex > 0) {
            return list.get(currentIndex--);
        } else {
            currentIndex = list.size() - 1;
            return list.getLast();
        }
    }

    public final MovingStatus getMovingStatus() {
        return movingStatus;
    }

    public final void setMovingStatus(final MovingStatus movingStatus) {
        this.movingStatus = movingStatus;
    }
}
