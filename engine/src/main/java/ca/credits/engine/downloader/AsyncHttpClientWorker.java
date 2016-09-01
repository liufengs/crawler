package ca.credits.engine.downloader;

import ca.credits.base.engine.AbstractWorker;
import ca.credits.base.engine.IWorkerManager;
import org.asynchttpclient.*;

/**
 * Created by chenwen on 16/8/31.
 */
public class AsyncHttpClientWorker extends AbstractWorker<AsyncHttpClientRequest> {
    private AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
    /**
     * create worker with workerManager
     * @param workerManager workManager
     */
    public AsyncHttpClientWorker(IWorkerManager<AsyncHttpClientRequest> workerManager) {
        super(workerManager);
    }

    /**
     * do work
     * @param task task
     */
    @Override
    protected void doWork(AsyncHttpClientRequest task) {
        if (asyncHttpClient.isClosed()){
            asyncHttpClient = new DefaultAsyncHttpClient();
        }

        task.getListener().onStart(task.getListener(),null);

        asyncHttpClient.executeRequest(task.getRequest(), new AsyncCompletionHandler<Response>(){
            @Override
            public Response onCompleted(Response response) throws Exception{
                task.getListener().onComplete(task.getListener(),new AsyncHttpClientResponse(task.getListener(),task,response));
                return response;
            }

            @Override
            public void onThrowable(Throwable t){
                task.getListener().onThrowable(task.getListener(),t,null);
            }
        });
    }
}
