package ca.credits.base;

import ca.credits.base.event.IEvent;
import ca.credits.base.gateway.IGateway;
import ca.credits.base.task.ITask;
import ca.credits.common.ListUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenwen on 16/8/26.
 */
public abstract class AbstractExecutive implements IExecutive {
    /**
     * activityId
     */
    protected String activityId;

    /**
     * id
     */
    protected String id;

    /**
     * all listener
     */
    protected Vector<IListener> listeners;

    /**
     * event status, default not start
     */
    protected AtomicInteger status;

    /**
     * the event child event.
     * event1 -> event2 -> event3 , event3 is event2's child and event2 is event1's child
     */
    protected List<IExecutive> children;

    /**
     * the event child event. like children
     */
    protected List<IExecutive> parents;

    /**
     * the gateway
     */
    protected IGateway gateway;

    /**
     * regulator manager
     */
    protected IExecutiveManager regulator;

    /**
     * exception
     */
    protected Throwable throwable;

    /**
     * the default constructor
     */
    public AbstractExecutive(String activityId,String id,List<IExecutive> children,IGateway gateway,IExecutiveManager regulator){
        status = new AtomicInteger(Status.UNDO.ordinal());
        listeners = new Vector<>();
        this.id = id;
        this.activityId = activityId;
        this.children = children;
        this.gateway = gateway;
        this.regulator = regulator;
        if (ListUtil.isNotEmpty(children)){
            children.stream().forEach(this::registerListener);
            children.stream().forEach(child -> child.addParent(this));
        }
    }

    /**
     * when event start run
     * @param subject run event
     * @param args event args
     */
    @Override
    public void onStart(final ISubject subject, final Object args) {
        /**
         * start event by this, then change event status to running and notify all listener
         */
        if (subject == this){
            status.set(Status.RUNNING.ordinal());
            if (ListUtil.isNotEmpty(listeners)) {
                listeners.parallelStream().forEach(listener -> listener.onStart(subject, args));
            }
        }
    }

    /**
     * when subject complete
     * @param subject complete subject
     * @param args args
     */
    @Override
    public void onComplete(ISubject subject, Object args) {
        /**
         * complete event by this, then change event status to done and notify all listener
         */
        if (subject == this){
            status.set(Status.DONE.ordinal());
            /**
             * notify this regulator
             */
            if (regulator != null) {
                regulator.complete(this, args);
            }

            if (ListUtil.isNotEmpty(listeners)) {
                listeners.parallelStream().forEach(listener -> listener.onComplete(subject, args));
            }
        }else {
            /**
             * call gateway event complete,then check this event can run?
             */
            IGateway.GatewaySuggest suggest = gateway.suggest(this);

            switch (suggest) {
                case NEXT:
                    regulator.next(this);
                    break;
            }
        }
    }

    /**
     * then event run throw exception
     * @param subject exception event
     * @param throwable exception
     * @param args args
     */
    @Override
    public void onThrowable(ISubject subject, Throwable throwable, Object args) {
        if (subject == this){
            this.throwable = throwable;
            status.set(Status.EXCEPTION.ordinal());
            /**
             * notify this regulator
             */
            if (regulator != null) {
                regulator.exception(this, throwable, args);
            }
            if (ListUtil.isNotEmpty(listeners)) {
                listeners.parallelStream().forEach(listener -> listener.onThrowable(subject, throwable, args));
            }
        }else {
            /**
             * call gateway event complete,then check this event can run?
             */
            IGateway.GatewaySuggest suggest = gateway.suggest(this);

            switch (suggest){
                case NEXT:
                    regulator.next(this);
                    break;
                case EXCEPTION:
                    this.onThrowable(this,throwable,args);
                    break;
            }
        }
    }

    /**
     * register listener
     * @param listener listener
     */
    @Override
    public void registerListener(IListener listener){
        if (listener == null)
            throw new NullPointerException();

        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * remove listener
     * @param listener listener
     */
    @Override
    public void removeListener(IListener listener){
        listeners.removeElement(listener);
    }

    /**
     * remove all listener
     */
    @Override
    public void removeAll(){
        listeners.removeAllElements();
    }

    @Override
    public String getActivityId() {
        return activityId;
    }

    @Override
    public String getTaskId() {
        return this instanceof ITask ? id : this instanceof IEvent ? ((ITask)regulator).getTaskId() : null;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<IExecutive> getParents() {
        return parents;
    }

    @Override
    public void addParent(IExecutive executive){
        if (parents == null){
            parents = new ArrayList<>();
        }
        parents.add(executive);
    }

    @Override
    public Status getStatus() {
        return Status.getValue(status.get());
    }
}
