package ca.credits.base.task;

import ca.credits.base.*;
import ca.credits.base.concurrent.ICountDownLatch;
import ca.credits.base.concurrent.StandaloneCountDownLatch;
import ca.credits.base.concurrent.TryLockTimeoutException;
import ca.credits.base.diagram.*;
import ca.credits.base.event.IEvent;
import ca.credits.base.gateway.DefaultGateway;
import ca.credits.base.gateway.IGateway;
import lombok.extern.slf4j.Slf4j;
import parsii.tokenizer.ParseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * Created by chenwen on 16/8/26.
 */
@Slf4j
public abstract class AbstractTask extends AbstractExecutive implements ITask {
    /**
     * the timeout,ms
     */
    protected long timeout = Long.MAX_VALUE;

    /**
     * the timeUnit
     */
    protected TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * lock
     */
    protected Lock lock = ComponentFactory.getLock(getLockKey());

    /**
     * the get lock time
     */
    protected long time = 60000;

    /**
     * the start event
     */
    protected IEvent startEvent;

    /**
     * the end event
     */
    protected IEvent endEvent;

    /**
     * the map of executive
     */
    protected Map<String,IExecutive> executiveMap;

    /**
     * 计数器,当计数器大于0时,将会阻塞.
     */
    private ICountDownLatch countDownLatch;


    public AbstractTask(String activityId,AbstractNode node,IExecutiveManager regulator){
        super(activityId,node,regulator);
        this.countDownLatch = ComponentFactory.tryCountDownLatch(1);
        this.executiveMap = new HashMap<>();
    }

    @Override
    public void complete(IExecutive executive, Object args) {
        /**
         * if endEvent is not null and endEvent complete, then task is completed
         */
        if (isEndEvent(executive)){
            this.onComplete(this, args);
        }else if (executive == this){
            /**
             * this task is complete
             */
            countDownLatch.countDown();
        }else {
            /**
             * the executive's children can run ?
             */
            suggest(executive,null,args);
        }
    }

    @Override
    public void next(IExecutive executive) {
        /**
         * if executive is task, then task start run task
         */
        if (!isComplete()) {
            executive.run();
        }
    }

    @Override
    public void exception(IExecutive executive, Throwable throwable, Object args) {
        /**
         * if key node throw exception,then this task throw exception
         */
        if (executive.getNode().isKeyNode()){
            this.onThrowable(this,throwable,args);
        }else if (executive == this){
            /**
             * this task is exception exit
             */
            countDownLatch.countDown();
        }else {
            log.error(String.format("id = %s exception",executive.getId()),throwable);
            suggest(executive,throwable,args);
        }
    }

    @Override
    public void run() {
        /**
         * step 1: if regulator is null , then regulator is self
         */
        if (regulator == null){
            regulator = this;
        }

        /**
         * step 2: get start node and start node
         */

        IExecutive executive = getIExecutive(getNode().getDag().getStartAbstractNode());

        if (executive == null){
            return;
        }

        this.onStart(this,null);

        executive.run();

        /**
         * step 3: if event task will sync wait node complete
         */
        if (regulator == this) {
            new Waiter(this).run();
        }else {
            new Thread(new Waiter(this)).start();
        }
    }

    /**
     * waiter
     */
    class Waiter implements Runnable{
        private IExecutive executive;

        public Waiter(IExecutive executive){
            this.executive = executive;
        }
        @Override
        public void run() {
            try {
                countDownLatch.await(timeout, timeUnit);
            } catch (InterruptedException e) {
                log.error("task InterruptedException", e);
                executive.onThrowable(executive, e, null);
            }
        }
    }

    @Override
    public IEvent getStartEvent() {
        return startEvent;
    }

    @Override
    public IEvent getEndEvent() {
        return endEvent;
    }

    @Override
    public void setStartEvent(IEvent startEvent) {
        this.startEvent = startEvent;
    }

    @Override
    public void setEndEvent(IEvent endEvent) {
        this.endEvent = endEvent;
    }

    @Override
    public AbstractTaskNode getNode(){
        return (AbstractTaskNode) node;
    }

    /**
     * the next suggest
     * @param executive executive
     * @param throwable throwable
     * @param args args
     */
    private void suggest(IExecutive executive,Throwable throwable, Object args){
        List<AbstractNode> children = executive.getNode().getChildren();
        children.parallelStream().forEach(child -> {
            List<AbstractNode> parents = child.getParents();
            for(AbstractNode parent : parents){
                if (!executiveMap.containsKey(parent.getId()) || !executiveMap.get(parent.getId()).isComplete()){
                    return;
                }
            }
            List<IExecutive> parentsExecutive = parents.parallelStream().map(parent -> executiveMap.get(parent.getId())).collect(Collectors.toList());
            IGateway gateway = new DefaultGateway(child.getGateway());
            IExecutive childExecutive = null;
            try {
                IGateway.GatewaySuggest suggest = gateway.suggest(parentsExecutive);
                switch (suggest){
                case NEXT:
                    childExecutive = getIExecutive(child);
                    if (childExecutive != null) {
                        this.next(childExecutive);
                    }
                    break;
                case EXCEPTION:
                    childExecutive = getIExecutive(child);
                    if (childExecutive != null) {
                        childExecutive.onThrowable(childExecutive, throwable, args);
                    }
                    break;
                }
            } catch (ParseException e) {
                childExecutive = getIExecutive(child);
                if (childExecutive != null) {
                    childExecutive.onThrowable(childExecutive, e, args);
                }
            }
        } );
    }

    /**
     * create executive from ExecutiveFactory, if executive already has been created, then return null
     * @param node node
     * @return executive
     */
    private IExecutive getIExecutive(AbstractNode node){
        try {
            if (lock.tryLock(time,timeUnit)) {
                if (executiveMap.containsKey(node.getId())) {
                    return null;
                }
                IExecutive result = ExecutiveFactory.createExecutive(node, activityId, this);
                if (result != null) {
                    executiveMap.put(node.getId(), result);
                }
                return result;
            }else {
                throw new TryLockTimeoutException("get lock error lockKey = " + getLockKey(),time,timeUnit);
            }
        } catch (InterruptedException | InvocationException | TryLockTimeoutException e) {
            this.onThrowable(this,e,null);
        } finally {
            if (lock != null){
                lock.unlock();
            }
        }
        return null;
    }

    /**
     * executive is end event
     * @param executive executive
     * @return
     */
    private boolean isEndEvent(IExecutive executive){
        return getNode().getDag().getEndAbstractNode() != null && executive.getId().equals(getNode().getDag().getEndAbstractNode().getId());
    }

    /**
     * get lock key
     * @return lock key
     */
    private String getLockKey(){
        return String.format("%s_%s_%s_%s",this.getClass().getName() ,activityId, getId(), UUID.randomUUID().toString());
    }
}
