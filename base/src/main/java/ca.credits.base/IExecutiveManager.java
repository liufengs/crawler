package ca.credits.base;

/**
 * Created by chenwen on 16/8/26.
 */
public interface IExecutiveManager {
    /**
     * complete
     */
    void complete(IExecutive executive,Object args);

    /**
     * exec next point
     * @param executive next point
     */
    void next(IExecutive executive);

    /**
     * throw exception
     * @param executive regulator running exception
     * @param throwable exception
     * @param args args
     */
    void exception(IExecutive executive, Throwable throwable, Object args);
}
