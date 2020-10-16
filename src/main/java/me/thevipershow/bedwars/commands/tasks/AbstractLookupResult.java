package me.thevipershow.bedwars.commands.tasks;

import java.util.Optional;

public abstract class AbstractLookupResult<I, T> {

    private final I interested;

    public AbstractLookupResult(final I interested) {
        this.interested = interested;
    }

    public abstract Optional<T> getLookupResult();

    public I getInterested() {
        return interested;
    }
}
