package ca.credits.base.diagram;

import ca.credits.base.event.DefaultEvent;

/**
 * Created by chenwen on 16/9/1.
 */
public class DefaultEventNode extends AbstractEventNode {
    /**
     * the creator
     * @param id id
     * @return
     */
    public static DefaultEventNode create(String id,String workerClass){
        return new DefaultEventNode(id,workerClass,false);
    }


    public static DefaultEventNode create(String id,String workerClass,boolean isKeyNode){
        return new DefaultEventNode(id,workerClass,isKeyNode);
    }

    /**
     * default constructor
     * @param id id
     */
    protected DefaultEventNode(String id,String workerClass,boolean isKeyNode) {
        super(id,workerClass,isKeyNode);
    }

    @Override
    public Class<DefaultEvent> getBelong() {
        return DefaultEvent.class;
    }

    @Override
    public String toString() {
        return "DefaultEventNode{}";
    }
}
