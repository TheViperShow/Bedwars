package me.thevipershow.aussiebedwars.commands.tasks;

import java.util.Optional;

public abstract class AbstractTargetCreator<I, T, L extends AbstractLookupResult<I, T>> {

    protected final I interested;
    protected final AbstractLookupResult<I, T> lookupResult;

    public AbstractTargetCreator(final I interested, L t) {
        this.interested = interested;
        this.lookupResult = t;
    }

    public abstract void create();

    public I getInterested() {
        return interested;
    }

    public AbstractLookupResult<I, T> getLookupResult() {
        return lookupResult;
    }
}
