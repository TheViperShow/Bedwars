package me.thevipershow.aussiebedwars.commands.arguments;

import java.util.function.Consumer;

public abstract class AbstractPerformer<T> {

    protected Consumer<T> executionLogic = null;
    protected final T t;

    public AbstractPerformer(T t) {
        this.t = t;
    }

    public boolean perform() {
        if (isEmptyLogic()) return false;
        executionLogic.accept(t);
        executionLogic = null;
        return true;
    }

    public Consumer<T> getExecutionLogic() {
        return executionLogic;
    }

    protected abstract void setExecutionLogic();

    public boolean isEmptyLogic() {
        return executionLogic == null;
    }

    public T getT() {
        return t;
    }
}
