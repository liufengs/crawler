package ca.credits.base;

/**
 * Created by chenwen on 16/8/26.
 */
public interface IListener{
    /**
     * when event start run
     */
    void onStart(ISubject subject,Object args);

    /**
     * when event success run
     */
    void onComplete(ISubject subject,Object args);

    /**
     * when throw Exception on event running
     */
    void onThrowable(ISubject subject,Throwable throwable,Object args);
}
