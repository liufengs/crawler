package ca.credits.base.engine;

import ca.credits.base.balance.IBalanceManager;
import ca.credits.base.event.IEvent;
import ca.credits.base.event.LoggerEvent;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by chenwen on 16/8/29.
 */
@Slf4j
@Named("loggerManagerEngine")
@Singleton
public class LoggerWorkerManager extends AbstractWorkerManager<LoggerEvent,LoggerWorker>{

    /**
     * the default workers count
     */
    private final static int DEFAULT_WORKERS_COUNT = 1;

    /**
     * default constructor
     */
    @Inject
    public LoggerWorkerManager(IEngine engine, IBalanceManager balanceManager){
        super(engine,balanceManager,LoggerWorker.class,DEFAULT_WORKERS_COUNT);
    }

    /**
     * add event
     * @param event event
     */
    @Override
    public void addTask(IEvent event) {
        try {
            queue.put((LoggerEvent) event);
        } catch (InterruptedException e) {
            event.onThrowable(event,e,null);
        }
    }

    /**
     * create worker
     * @return worker
     */
    @Override
    protected AbstractWorker createWorker() {
        return new LoggerWorker(this);
    }
}
