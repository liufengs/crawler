package ca.credits.base;

import java.util.List;

/**
 * Created by chenwen on 16/8/26.
 */
public interface IExecutive extends ISubject,IListener{
    /**
     * start run
     */
    void run();
    /**
     * get activity id
     * @return activity id
     */
    String getActivityId();

    /**
     * get task id
     * @return task id
     */
    String getTaskId();

    /**
     * get id
     * @return id
     */
    String getId();

    /**
     * get all parents
     * @return parents
     */
    List<IExecutive> getParents();

    /**
     * add parent
     * @param executive parent
     */
    void addParent(IExecutive executive);

    /**
     * get status
     * @return status
     */
    Status getStatus();

    /**
     * regulator status
     */
    enum Status{
        /**
         * regulator not start run
         */
        UNDO,
        /**
         * regulator running
         */
        RUNNING,
        /**
         * regulator complete success
         */
        DONE,
        /**
         * regulator complete exception
         */
        EXCEPTION;

        public static Status getValue(int i){
            for(Status status : values()){
                if (status.ordinal() == i){
                    return status;
                }
            }
            return null;
        }
    }
}
