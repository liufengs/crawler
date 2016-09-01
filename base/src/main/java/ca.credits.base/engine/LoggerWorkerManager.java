package ca.credits.base.engine;

import ca.credits.base.event.IEvent;
import ca.credits.base.event.LoggerEvent;
import ca.credits.base.queue.IQueue;
import ca.credits.base.queue.StandaloneQueue;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chenwen on 16/8/29.
 */
@Slf4j
@Named("loggerManagerEngine")
@Singleton
public class LoggerWorkerManager extends AbstractWorkerManager<LoggerEvent>{

    /**
     * queue
     */
    protected IQueue<LoggerEvent> queue = new StandaloneQueue<>(LoggerEvent.class);

    /**
     * workers
     */
    protected List<AbstractWorker> workers = new ArrayList<>();

    /**
     * engine
     */
    private final IEngine engine;

    /**
     * default constructor
     */
    @Inject
    public LoggerWorkerManager(IEngine engine){
        log.info("start init " + LoggerWorkerManager.class.getName());
        /**
         * worker class register
         */
        this.engine = engine;
        register(LoggerWorker.class);

        for(int i = 0; i < 10; ++i){
            workers.add(new LoggerWorker(this));
        }
        workers.parallelStream().forEach(Thread::start);
    }

    /**
     * add event
     * @param event event
     */
    @Override
    public void add(IEvent event) {
        try {
            queue.put((LoggerEvent) event);
        } catch (InterruptedException e) {
            event.onThrowable(event,e,null);
        }
    }

    /**
     * worker is done , then remove worker from worker list
     * @param worker
     */
    @Override
    public void done(AbstractWorker worker) {
        workers.remove(worker);
    }

    /**
     * take task
     * @return task
     * @throws InterruptedException exception
     */
    @Override
    public LoggerEvent take(AbstractWorker worker) throws InterruptedException {
        return queue.take();
    }

    /**
     * get engine
     * @return engine
     */
    @Override
    public IEngine getEngine() {
        return engine;
    }
}
