package ca.credits.base.diagram;

import ca.credits.base.event.LoggerEvent;
import ca.credits.base.task.DefaultTask;

/**
 * Created by chenwen on 16/9/1.
 */
public class DefaultEventNode extends AbstractEventNode {
    /**
     * the creator
     * @param id id
     * @return
     */
    public static DefaultEventNode create(String id){
        return new DefaultEventNode(id,false);
    }


    public static DefaultEventNode create(String id,boolean isKeyNode){
        return new DefaultEventNode(id,isKeyNode);
    }

    /**
     * default constructor
     * @param id id
     */
    protected DefaultEventNode(String id,boolean isKeyNode) {
        super(id,isKeyNode);
    }

    @Override
    public Class<LoggerEvent> getBelong() {
        return LoggerEvent.class;
    }

    @Override
    public String toString() {
        return "DefaultEventNode{}";
    }
}
