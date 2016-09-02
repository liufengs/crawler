package ca.credits.base.diagram;

/**
 * Created by chenwen on 16/9/1.
 */
public abstract class AbstractEventNode extends AbstractNode{
    /**
     * default constructor
     * @param id id
     */
    protected AbstractEventNode(String id) {
        this(id,false);
    }


    protected AbstractEventNode(String id,boolean isKeyNode) {
        super(id,isKeyNode);
    }
}
