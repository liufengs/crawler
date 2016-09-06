package ca.credits.engine.downloader;

import ca.credits.base.balance.IBalanceManager;
import ca.credits.base.engine.AbstractWorker;
import ca.credits.base.engine.AbstractWorkerManager;
import ca.credits.base.engine.IEngine;
import ca.credits.base.event.IEvent;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by chenwen on 16/8/31.
 */
@Named("asyncHttpClientWorkerManager")
@Singleton
public class AsyncHttpClientWorkerManager extends AbstractWorkerManager<AsyncHttpClientRequest,AsyncHttpClientWorker>{
    @Inject
    public AsyncHttpClientWorkerManager(IEngine engine, IBalanceManager balanceManager){
        super(engine,balanceManager,AsyncHttpClientWorker.class,1);
    }

    @Override
    protected void addTask(IEvent event) {
        try {
            queue.put(new AsyncHttpClientRequest(event));
        } catch (InterruptedException e) {
            event.onThrowable(event,e,null);
        }
    }

    @Override
    protected AbstractWorker createWorker() {
        return new AsyncHttpClientWorker(this);
    }
}
