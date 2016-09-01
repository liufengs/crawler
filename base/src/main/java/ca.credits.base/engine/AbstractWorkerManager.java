package ca.credits.base.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chenwen on 16/8/30.
 */
public abstract class AbstractWorkerManager<E> implements IWorkerManager<E>{
    /**
     * worker map
     */
    protected Map<String,Boolean> workerMap = new ConcurrentHashMap<>();

    /**
     * register work class
     * @param workerClass worker
     */
    protected  <T extends AbstractWorker> void register(Class<T> workerClass) {
        if (!workerMap.containsKey(workerClass.getName())){
            workerMap.put(workerClass.getName(),true);
            getEngine().register(this,workerClass);
        }
    }

    /**
     * get engine
     * @return engine
     */
    protected abstract IEngine getEngine();
}
