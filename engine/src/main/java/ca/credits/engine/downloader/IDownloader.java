package ca.credits.engine.downloader;

import ca.credits.engine.IRequest;
import ca.credits.engine.IResponse;

/**
 * Created by chenwen on 16/8/19.
 */
public interface IDownloader {
    IResponse download(IRequest iRequest);
}
