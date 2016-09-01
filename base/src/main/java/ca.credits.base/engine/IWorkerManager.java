package ca.credits.base.engine;

import ca.credits.base.event.IEvent;

/**
 * Created by chenwen on 16/8/30.
 */
public interface IWorkerManager<T> {
    /**
     * poll event
     * @return event
     */
    T take(AbstractWorker worker) throws InterruptedException;

    /**
     * add event
     * @param event event
     */
    void add(IEvent event);

    /**
     * when worker is shutdown
     * @param worker
     */
    void done(AbstractWorker worker);
}
