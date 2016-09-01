package ca.credits.base;

import ca.credits.base.event.IEvent;
import ca.credits.base.event.LoggerEvent;
import ca.credits.base.task.DefaultTask;
import ca.credits.base.task.ITask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by chenwen on 16/8/29.
 */
@Slf4j
public class DefaultTaskTest{


    /**
     *
     * <activity id="loggerEngineTest">
     *      <task id="task1">
     *          <event id="task1.startEvent" next="task1.event1">
     *          </event>
     *
     *          <event id="task1.event1" next="task2">
     *          </event>
     *
     *          <task id="task2" next="task1.endEvent">
     *              <event id="task2.startEvent" next="task2.event1">
     *              </event>
     *
     *              <event id="task2.event1" next="task2.endEvent">
     *              </event>
     *
     *              <event id="task2.endEvent" next="end">
     *              </event>
     *          </task>
     *
     *          <event id="task1.endEvent" next="end">
     *          </event>
     *      </task>
     * </activity>
     *
     *
     */
    @Test
    public void testLoggerEngine(){

        final String activityId = "loggerEngineTest";

        final String task1Id = "task1";

        final String task2Id = "task2";


        ITask task1 = new DefaultTask(activityId,task1Id,null,null);

        IEvent task1EndEvent = new LoggerEvent("task1.endEvent",null,task1);

        ITask task2 = new DefaultTask(activityId,task2Id, Collections.singletonList(task1EndEvent),task1);

        IEvent task2EndEvent = new LoggerEvent("task2.endEvent",null,task2);

        IEvent task2Event1 = new LoggerEvent("task2.event1",Collections.singletonList(task2EndEvent),task2);

        IEvent task2StartEvent = new LoggerEvent("task2.startEvent",Collections.singletonList(task2Event1),task2);

        task2.setStartEvent(task2StartEvent);
        task2.setEndEvent(task2EndEvent);

        IEvent task1Event1 = new LoggerEvent("task1.event1",Collections.singletonList(task2),task1);

        IEvent task1StartEvent = new LoggerEvent("task1.startEvent",Collections.singletonList(task1Event1),task1);

        task1.setStartEvent(task1StartEvent);
        task1.setEndEvent(task1EndEvent);

        task1.run();
    }

    /**
     * 多事件并发
     * <activity id="loggerEngineTest">
     *      <task id="task1">
     *          <event id="task1.event1" next="task1.endEvent">
     *          </event>
     *
     *          <event id="task1.event2" next="task1.endEvent">
     *          </event>
     *
     *          <event id="task1.event3" next="task1.endEvent">
     *          </event>
     *
     *          <event id="task1.endEvent" next="end">
     *          </event>
     *      </task>
     * </activity>
     *
     *
     */
    @Test
    public void testConcurrentLoggerEngine(){
        final String activityId = "loggerEngineTest";

        final String task1Id = "task1";


        ITask task1 = new DefaultTask(activityId,task1Id,null,null);

        IEvent task1EndEvent = new LoggerEvent("task1.endEvent",null,task1);

        IEvent event3 = new LoggerEvent("task1.event3",Collections.singletonList(task1EndEvent),task1);

        IEvent event2 = new LoggerEvent("task1.event2",Collections.singletonList(task1EndEvent),task1);

        IEvent event1 = new LoggerEvent("task1.event1",Collections.singletonList(task1EndEvent),task1);

        IEvent task1StartEvent = new LoggerEvent("task1.startEvent",new ArrayList<IExecutive>(){{
            add(event1);
            add(event2);
            add(event3);
        }},task1);

        task1.setStartEvent(task1StartEvent);
        task1.setEndEvent(task1EndEvent);

        task1.run();
    }
}
