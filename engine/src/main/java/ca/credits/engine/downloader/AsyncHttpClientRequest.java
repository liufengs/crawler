package ca.credits.engine.downloader;

import ca.credits.base.event.IEvent;
import ca.credits.common.Properties;
import ca.credits.engine.IRequest;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.RequestBuilderBase;

/**
 * Created by chenwen on 16/8/31.
 */
public class AsyncHttpClientRequest implements IRequest {
    /**
     * event listener
     */
    private IEvent listener;

    /**
     * async http request
     */
    private Request request;

    public AsyncHttpClientRequest(IEvent listener){
        this.listener = listener;
        buildRequest();
    }

    /**
     * build async http request
     */
    private void buildRequest(){
        Properties properties = listener.getNode().getProperties();

        RequestBuilderBase<RequestBuilder> requestBuilder = new RequestBuilder(properties.getString("method"));

        requestBuilder.setUrl(properties.getString("url"));

        request = requestBuilder.build();
    }

    /**
     * get request
     * @return request
     */
    public Request getRequest() {
        return request;
    }

    /**
     * get event
     * @return event
     */
    public IEvent getListener() {
        return listener;
    }
}
