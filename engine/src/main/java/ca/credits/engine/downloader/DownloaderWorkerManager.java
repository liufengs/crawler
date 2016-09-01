package ca.credits.engine.downloader;

import ca.credits.base.engine.AbstractWorker;
import ca.credits.base.engine.AbstractWorkerManager;
import ca.credits.base.engine.IEngine;
import ca.credits.base.event.IEvent;
import ca.credits.base.queue.IQueue;
import ca.credits.base.queue.StandaloneQueue;
import ca.credits.engine.IRequest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenwen on 16/8/31.
 */
@Named("downloaderWorkerManager")
@Singleton
public class DownloaderWorkerManager<T extends IRequest> extends AbstractWorkerManager<T>{
    /**
     * engine
     */
    private final IEngine engine;

    private Map<String,IQueue<AsyncHttpClientRequest>> queueMap = new HashMap<>();

    private IQueue<AsyncHttpClientRequest> asyncHttpClientRequestQueue = new StandaloneQueue<>(AsyncHttpClientWorker.class);

    @Inject
    public DownloaderWorkerManager(IEngine engine){
        this.engine = engine;
        register(AsyncHttpClientWorker.class);
        queueMap.put(AsyncHttpClientWorker.class.getName().toLowerCase(),asyncHttpClientRequestQueue);
        queueMap.put(AsyncHttpClientWorker.class.getSimpleName().toLowerCase(),asyncHttpClientRequestQueue);
    }

    @Override
    protected IEngine getEngine() {
        return engine;
    }

    @Override
    public T take(AbstractWorker worker) throws InterruptedException {
        return (T) queueMap.get(worker.getClass().getName()).take();
    }

    @Override
    public void add(IEvent event) {
        queueMap.get(event.getWorkerClassName());
    }

    @Override
    public void done(AbstractWorker worker) {
    }
}
