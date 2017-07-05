package bacta.io.service.scheduler;

import java.util.concurrent.Future;

/**
 * Created by Kyle on 9/5/2014.
 */
public abstract class Task<EventType> implements Runnable {

    protected Future<?> future = null;


    protected void setFuture(Future<?> future) {
        this.future = future;
    }

    public boolean cancel(boolean canInterrupt) {
        return future.cancel(canInterrupt);
    }
}
