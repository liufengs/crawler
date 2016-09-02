package ca.credits.base.event;

import ca.credits.base.diagram.AbstractNode;
import ca.credits.base.engine.LoggerWorker;
import ca.credits.base.gateway.DefaultGateway;
import ca.credits.base.IExecutive;
import ca.credits.base.task.ITask;

import java.util.List;

/**
 * Created by chenwen on 16/8/29.
 */
public class LoggerEvent extends AbstractEvent {
    public LoggerEvent(AbstractNode node, ITask regulator){
        super(LoggerWorker.class.getName(),regulator.getActivityId(),node,regulator);
    }
}
