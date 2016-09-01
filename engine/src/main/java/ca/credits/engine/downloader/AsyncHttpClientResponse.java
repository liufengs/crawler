package ca.credits.engine.downloader;

import ca.credits.base.event.IEvent;
import ca.credits.engine.IRequest;
import ca.credits.engine.IResponse;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

/**
 * Created by chenwen on 16/8/31.
 */
public class AsyncHttpClientResponse implements IResponse {
    /**
     * event listener
     */
    private IEvent listener;

    /**
     * async http request
     */
    private Response response;

    /**
     * request
     */
    private IRequest request;

    public AsyncHttpClientResponse(IEvent listener,IRequest request,Response response){
        this.listener = listener;
        this.request = request;
        this.response = response;
    }

    public IEvent getListener() {
        return listener;
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public IRequest getRequest() {
        return request;
    }
}
