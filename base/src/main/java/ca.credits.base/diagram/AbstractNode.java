package ca.credits.base.diagram;

import ca.credits.base.IExecutive;
import ca.credits.common.ListUtil;
import ca.credits.common.Properties;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by chenwen on 16/9/1.
 * this class is the node of event stream
 */
public abstract class AbstractNode {
    /**
     * the node id
     */
    @Getter
    private String id;

    /**
     * the node name
     */
    @Getter
    private String name;

    /**
     * the node gateway
     */
    @Getter
    private String gateway;

    /**
     * the timeout , time unit , ms
     */
    @Getter
    private long timeout;

    /**
     * the children nodes
     */
    private Map<String,AbstractNode> children;

    /**
     * the parents nodes
     */
    private Map<String,AbstractNode> parents;

    /**
     * the properties
     */
    @Getter
    private Properties properties;

    /**
     * is key node
     */
    @Getter
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
        children = new ConcurrentHashMap<>();
        parents = new ConcurrentHashMap<>();
        properties = new Properties();
    }

    public AbstractNode name(String name){
        this.name = name;
        return this;
    }

    public AbstractNode gateway(String gateway){
        this.gateway = gateway;
        return this;
    }

    public AbstractNode keyNode(boolean isKeyNode){
        this.isKeyNode = isKeyNode;
        return this;
    }

    public AbstractNode timeout(long timeout){
        this.timeout = timeout;
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
    public AbstractNode addChildren(Collection<AbstractNode> children){
        children.stream().forEach(node -> this.children.putIfAbsent(node.getId(),node));
        return this;
    }

    /**
     * add parents
     * @param parents parents
     * @return
     */
    public AbstractNode addParents(Collection<AbstractNode> parents){
        parents.stream().forEach(node -> this.parents.putIfAbsent(node.getId(),node));
        return this;
    }

    /**
     * remove node
     * @param node removed node
     */
    public void removeParent(AbstractNode node){
        this.parents.remove(node.getId());
    }

    /**
     * remove node
     * @param node removed node
     */
    public void removeChild(AbstractNode node){
        this.children.remove(node.getId());
    }

    /**
     * node destroy
     */
    public void destroy(){
        try {
            this.children.keySet().stream().map(key -> this.children.get(key)).forEach(node -> node.removeParent(this));
            this.parents.keySet().stream().map(key -> this.parents.get(key)).forEach(node -> node.removeChild(this));
            this.children = null;
            this.parents = null;
            this.properties = null;
        }catch (Exception e){
        }
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
        if (children == null || ListUtil.isEmpty(children.keySet())){
            return getId();
        }

        StringBuilder stringBuilder = new StringBuilder();
        children.keySet().stream().map(key -> this.children.get(key)).forEach(child -> {
            if (stringBuilder.length() == 0){
                stringBuilder.append("( ").append(child.show());
            }else {
                stringBuilder.append(" , ").append(child.show());
            }
        });
        stringBuilder.append(" )");

        return String.format("%s -> %s",this.getId(),stringBuilder.toString());
    }

    /**
     * add property
     */
    public AbstractNode property(String key,Object value){
        properties.put(key,value);
        return this;
    }

    public Collection<AbstractNode> getChildren() {
        return children.keySet().stream().map(key -> this.children.get(key)).collect(Collectors.toList());
    }

    public Collection<AbstractNode> getParents() {
        return parents.keySet().stream().map(key -> this.parents.get(key)).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractNode node = (AbstractNode) o;

        return id != null ? id.equals(node.id) : node.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
