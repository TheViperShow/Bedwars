package me.thevipershow.aussiebedwars.commands.tasks;

public abstract class AbstractTargetInteractor<I, T, L extends AbstractLookupResult<I, T>> {

    protected final I interested;
    protected final AbstractLookupResult<I, T> lookupResult;

    public AbstractTargetInteractor(final I interested, L t) {
        this.interested = interested;
        this.lookupResult = t;
    }

    public abstract void perform();

    public I getInterested() {
        return interested;
    }

    public AbstractLookupResult<I, T> getLookupResult() {
        return lookupResult;
    }
}
