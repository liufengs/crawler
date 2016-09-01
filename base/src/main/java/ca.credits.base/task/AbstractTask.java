package ca.credits.base.task;

import ca.credits.base.*;
import ca.credits.base.concurrent.ICountDownLatch;
import ca.credits.base.event.IEvent;
import ca.credits.base.gateway.IGateway;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
     * the start event
     */
    protected IEvent startEvent;

    /**
     * the end event
     */
    protected IEvent endEvent;

    /**
     * 计数器,当计数器大于0时,将会阻塞.
     */
    private ICountDownLatch countDownLatch;

    public AbstractTask(IEvent startEvent, IEvent endEvent, IGateway gateway, ICountDownLatch countDownLatch, String activityId, String id, List<IExecutive> children, IExecutiveManager regulator){
        super(activityId,id,children,gateway,regulator);
        this.startEvent = startEvent;
        this.endEvent = endEvent;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void complete(IExecutive executive, Object args) {
        /**
         * if endEvent complete, then task complete
         */
        if (executive == endEvent){
            this.onComplete(this,args);
        }else if (executive == this){
            /**
             * 当前task已经完成
             */
            countDownLatch.countDown();
        }
    }

    @Override
    public void next(IExecutive executive) {
        /**
         * if executive is task, then task start run task
         */
        executive.run();
    }

    @Override
    public void exception(IExecutive executive, Throwable throwable, Object args) {
        /**
         * if endEvent throw exception,then report exception
         */
        if (executive == endEvent){
            this.onThrowable(this,throwable,args);
        }else if (executive == this){
            /**
             * 当前task出现异常
             */
            countDownLatch.countDown();
        }else {
            log.error(String.format("id = %s exception",executive.getId()),throwable);
        }
    }

    @Override
    public void run() {
        /**
         * if regulator is null , then regulator is self
         */
        if (regulator == null){
            regulator = this;
        }
        /**
         * start run
         */
        startEvent.run();

        if (regulator == this) {
            try {
                countDownLatch.await(timeout, timeUnit);
            } catch (InterruptedException e) {
                log.error("task InterruptedException", e);
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
}
