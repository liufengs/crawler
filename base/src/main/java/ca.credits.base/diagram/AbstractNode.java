package ca.credits.base.diagram;

import ca.credits.base.IExecutive;
import ca.credits.common.ListUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chenwen on 16/9/1.
 * this class is the node of event stream
 */
@Data
public abstract class AbstractNode {
    /**
     * the node id
     */
    private String id;

    /**
     * the node name
     */
    private String name;

    /**
     * the node gateway
     */
    private String gateway;

    /**
     * the children nodes
     */
    private List<AbstractNode> children;

    /**
     * the parents nodes
     */
    private List<AbstractNode> parents;

    /**
     * is key node
     */
    private boolean isKeyNode;

    /**
     * the default is not key node
     * @param id id
     */
    protected AbstractNode(String id){
        this(id,false);
    }

    protected AbstractNode(String id,boolean isKeyNode){
        this.id = id;
        this.isKeyNode = isKeyNode;
        children = new ArrayList<>();
        parents = new ArrayList<>();
    }

    public AbstractNode withName(String name){
        this.name = name;
        return this;
    }

    public AbstractNode withGateway(String gateway){
        this.gateway = gateway;
        return this;
    }

    public AbstractNode withKeyNode(boolean isKeyNode){
        this.isKeyNode = isKeyNode;
        return this;
    }

    /**
     * add children
     * @param children children
     * @return
     */
    public AbstractNode addChildren(AbstractNode... children){
        return addChildren(Arrays.asList(children));
    }

    /**
     * add parents
     * @param parents parents
     * @return
     */
    public AbstractNode addParents(AbstractNode... parents){
        return addParents(Arrays.asList(parents));
    }

    /**
     * add children
     * @param children children
     * @return
     */
    public synchronized AbstractNode addChildren(List<AbstractNode> children){
        this.children.addAll(children);
        return this;
    }

    /**
     * add parents
     * @param parents parents
     * @return
     */
    public synchronized AbstractNode addParents(List<AbstractNode> parents){
        this.parents.addAll(parents);
        return this;
    }

    /**
     * the node belong executive
     */
    public abstract <T extends IExecutive> Class<T> getBelong();

    /**
     * show this
     * @return show
     */
    public String show(){
        if (ListUtil.isEmpty(children)){
            return getId();
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( ").append(children.get(0).show());
        children.stream().filter(child -> child != children.get(0)).forEach(child -> {
            stringBuilder.append(" , ").append(child.show());
        });
        stringBuilder.append(" )");

        return String.format("%s -> %s",this.getId(),stringBuilder.toString());
    }
}
