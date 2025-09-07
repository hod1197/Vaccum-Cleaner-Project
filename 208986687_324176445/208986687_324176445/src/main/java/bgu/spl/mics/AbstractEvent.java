package bgu.spl.mics;

public abstract class AbstractEvent<T> implements Event<T> {
    private Future<T> future;

    public AbstractEvent() {
        this.future = new Future<>();
    }

    public Future<T> getFuture() {
        return future;
    }

    public void setFuture(Future<T> future) {
        this.future = future;
    }
}
