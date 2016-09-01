package ca.credits.base.task;

import ca.credits.base.*;
import ca.credits.base.concurrent.StandaloneCountDownLatch;
import ca.credits.base.event.IEvent;
import ca.credits.base.gateway.DefaultGateway;

import java.util.List;

/**
 * Created by chenwen on 16/8/29.
 */
public class DefaultTask extends AbstractTask {
    public DefaultTask(String activityId, String id, List<IExecutive> children, IExecutiveManager regulator) {
        this(null, null,activityId, id, children, regulator);
    }
    public DefaultTask(IEvent startEvent, IEvent endEvent, String activityId, String id, List<IExecutive> children, IExecutiveManager regulator) {
        super(startEvent, endEvent, new DefaultGateway(null),new StandaloneCountDownLatch(1), activityId, id, children, regulator);
    }
}
