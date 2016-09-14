package ca.credits.base.task;

import ca.credits.base.*;
import ca.credits.base.concurrent.ICountDownLatch;
import ca.credits.base.concurrent.TryLockTimeoutException;
import ca.credits.base.diagram.*;
import ca.credits.base.event.IEvent;
import ca.credits.base.gateway.DefaultGateway;
import ca.credits.base.gateway.IGateway;
import ca.credits.base.kit.Constants;
import ca.credits.common.*;
import ca.credits.common.Properties;
import lombok.extern.slf4j.Slf4j;
import parsii.tokenizer.ParseException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * Created by chenwen on 16/8/26.
 */
@Slf4j
public abstract class AbstractTask extends AbstractExecutive implements ITask {
    /**
     * the lockTimeUnit
     */
    protected TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * lock
     */
    protected Lock lock = ComponentFactory.getLock(getLockKey());

    /**
     * the get lock lockTime
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
        this.executiveMap = new ConcurrentHashMap<>();
    }

    @Override
    public void complete(IExecutive executive, Properties args) {
        /**
         * step 1: if executive has child node , then add child node
         */
        final Collection<AbstractNode> childNodes = getChildNodes(args);
        if (ListUtil.isNotEmpty(childNodes)){
            /**
             * add all child nodes
             */
            childNodes.stream().forEach(node -> getNode().getDag().addEdges(
                    Edge.builder()
                            .id(String.format("%s.%s.%s",getId(),executive.getId(),executive.incrementAndGet()))
                            .source(executive.getNode())
                            .target(node)
                            .build())
            );
        }

        /**
         * step 2: destroy executive node
         */
        destroyExecutive(executive);

        /**
         * step 3: if dag has not nodes, then complete this task
         */
        if (executive == this){
            /**
             * this task is complete
             */
            countDownLatch.countDown();
        }else if (!runStartNode()){
            this.onComplete(this, null);
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
    public void exception(IExecutive executive, Throwable throwable, Properties args) {
        /**
         * step 1: destroy executive node
         */
        destroyExecutive(executive);

        /**
         * step 2: if this exception, then exit
         */
        if (executive == this){
            /**
             * this task is exception exit
             */
            countDownLatch.countDown();
        /**
         * if key node throw exception,then this task throw exception
         */
        }else if (executive.getNode().isKeyNode() || !runStartNode()){
            this.onThrowable(this,throwable,null);
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
        this.onStart(this,null);

        runStartNode();

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
     * run all start node
     */
    private boolean runStartNode(){
        Collection<AbstractNode> startNode = getNode().getDag().getStartNode();

        if (ListUtil.isEmpty(startNode)){
            return false;
        }

        AtomicLong tmp = new AtomicLong(0);

        startNode.parallelStream().forEach(node -> {
            IExecutive executive = getIExecutive(node);

            if (executive == null){
                tmp.incrementAndGet();
                return;
            }

            executive.run();
        });

        return tmp.get() <= 0;
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
                countDownLatch.await(executive.getNode().getTimeout() <= 0 ? time : executive.getNode().getTimeout(), timeUnit);
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
     */
    private void suggest(IExecutive executive,Throwable throwable){
        Collection<AbstractNode> children = executive.getNode().getChildren();
        children.parallelStream().forEach(child -> {
            Collection<AbstractNode> parents = child.getParents();
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
                            childExecutive.onThrowable(childExecutive, throwable, null);
                        }
                        break;
                }
            } catch (ParseException e) {
                childExecutive = getIExecutive(child);
                if (childExecutive != null) {
                    childExecutive.onThrowable(childExecutive, e, null);
                }
            }
        } );
    }

    /**
     * destroy nodes
     * @param executive completed executive
     */
//    private void destroyNodes(IExecutive executive){
//        /**
//         * step 2: get all parents, if parent's children is complete, then destroy parent node
//         */
//        Collection<AbstractNode> parents = executive.getNode().getParents();
//
//        parents.parallelStream().forEach(parent -> {
//            Collection<AbstractNode> children = parent.getChildren();
//
//            for(AbstractNode child : children){
//                if (!executiveMap.containsKey(child.getId()) || !executiveMap.get(child.getId()).isComplete()){
//                    return;
//                }
//            }
//
//            executiveMap.remove(parent.getId());
//
//            getNode().getDag().destroyNode(parent);
//        });
//
//        /**
//         * step 3: if executive node has not child node, then destroy executive node
//         */
//        if (ListUtil.isEmpty(executive.getNode().getChildren())){
//            executiveMap.remove(executive.getNode().getId());
//
//            getNode().getDag().destroyNode(executive.getNode());
//        }
//    }

    /**
     * executive completed
     * @param executive executive
     */
    private void destroyExecutive(IExecutive executive){
        executiveMap.remove(executive.getNode().getId());
        executive.getNode().destroy();
    }

    /**
     * get child nodes
     * @param args result
     * @return collection
     */
    private Collection<AbstractNode> getChildNodes(Properties args){
        if (args != null){
            return args.getCollection(Constants.childNodes);
        }
        return null;
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
     * can complete this task
     * @return can complete then true, or not false
     */
    private boolean canComplete(){
        return ListUtil.isEmpty(getNode().getDag().getNodes())
                || (getNode().getDag().getNodes().size() == 1);
    }

    /**
     * get lock key
     * @return lock key
     */
    private String getLockKey(){
        return String.format("%s_%s_%s_%s",this.getClass().getName() ,activityId, getId(), UUID.randomUUID().toString());
    }
}
