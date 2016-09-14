package ca.credits.engine.downloader;

import ca.credits.base.engine.AbstractWorker;
import ca.credits.base.engine.IWorkerManager;
import ca.credits.base.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.*;

import java.nio.charset.Charset;

/**
 * Created by chenwen on 16/8/31.
 */
@Slf4j
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
                log.info(StringUtils.formatExecutive(task.getListener(),response.getResponseBody(Charset.defaultCharset())));
//                task.getListener().onComplete(task.getListener(),new AsyncHttpClientResponse(task.getListener(),task,response));
                task.getListener().onComplete(task.getListener(),null);
                return response;
            }

            @Override
            public void onThrowable(Throwable t){
                task.getListener().onThrowable(task.getListener(),t,null);
            }
        });
    }
}
