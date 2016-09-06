package ca.credits.base.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chenwen on 16/8/30.
 * 这是一个单机版的queue
 */
public class StandaloneQueue<T> implements IQueue<T> {
    private BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    private String name;

    public StandaloneQueue(String name){
        this.name = name;
    }

    @Override
    public T take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public void put(T t) throws InterruptedException {
        queue.put(t);
    }

    @Override
    public int getLeastTask() {
        return queue.size();
    }

    @Override
    public String getName() {
        return name;
    }
}
