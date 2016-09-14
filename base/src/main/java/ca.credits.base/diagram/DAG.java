package ca.credits.base.diagram;

import ca.credits.base.ComponentFactory;
import ca.credits.base.concurrent.ConcurrentLockException;
import ca.credits.common.ListUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * Created by chenwen on 16/9/1.
 * this class is the diagram of event stream
 *
 * DAG 有向无环图
 */
@Slf4j
public class DAG {
    /**
     * all nodes
     */
    private Map<String,AbstractNode> nodes;

    /**
     * all edges
     */
    private Map<String,Edge> edges;

    /**
     * the lockTimeUnit
     */
    protected TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * lock
     */
    protected Lock lock = ComponentFactory.getLock(String.format("%s_%s",this.hashCode(), UUID.randomUUID()));

    /**
     * the get lock lockTime
     */
    protected long time = 60000;

    /**
     * default create method
     * @return DAG
     */
    public static DAG create(){
        return new DAG();
    }

    /**
     * default constructor
     */
    private DAG(){
        init();
    }

    private void init(){
        this.nodes = new ConcurrentHashMap<>();
        this.edges = new ConcurrentHashMap<>();
    }

    /**
     * add abstractNodes to diagram
     * @param edges edges
     * @return
     */
    public synchronized DAG addEdges(Collection<Edge> edges){
        edges.stream().forEach(edge -> {
            this.nodes.putIfAbsent(edge.getSource().getId(),edge.getSource());
            this.nodes.putIfAbsent(edge.getTarget().getId(),edge.getTarget());
            edge.getSource().addChildren(edge.getTarget());
            edge.getTarget().addParents(edge.getSource());
            this.edges.putIfAbsent(edge.getId(),edge);
        });
        return this;
    }

    /**
     * add edges to diagram
     * @param edges edges
     * @return
     */
    public DAG addEdges(Edge... edges){
        return addEdges(Arrays.asList(edges));
    }

//    /**
//     * destroy node
//     * @param node node
//     * @return this
//     */
//    public DAG destroyNode(AbstractNode node){
//        log.info("destroy = " + node.getId());
//        this.nodes.remove(node);
//        this.edges.stream().filter(edge -> edge.getSource().equals(node) || edge.getTarget().equals(node)).forEach(edge -> {
//            this.edges.remove(edge);
//        });
//        node.destroy();
//        return this;
//    }

    /**
     * show diagram
     */
    public String show(){
        StringBuilder result = new StringBuilder();
        getStartNode().stream().forEach(node -> result.append("[ ").append(node.show()).append( " ]"));
        return result.toString();
    }

    /**
     * get start node
     * @return start node
     */
    public Collection<AbstractNode> getStartNode() {
        try {
            if (lock.tryLock(time, timeUnit)) {
                Collection<AbstractNode> nodes = getNodes();
                Collection<Edge> edges = getEdges();
                Collection<AbstractNode> result = nodes.stream().filter(node -> ListUtil.isEmpty(node.getParents())).collect(Collectors.toList());
                for(AbstractNode node : result) {
                    edges.stream().filter(edge -> edge.getSource().equals(node) || edge.getTarget().equals(node)).forEach(edge -> this.edges.remove(edge.getId()));
                    this.nodes.remove(node.getId());
                }
                return result;
            }
        } catch (InterruptedException e) {
            throw new ConcurrentLockException("try lock failed",e);
        } finally {
          if (lock != null){
              lock.unlock();
          }
        }
        return null;
    }

    public Collection<AbstractNode> getNodes() {
        return nodes.keySet().stream().map(key -> nodes.get(key)).collect(Collectors.toList());
    }

    public Collection<Edge> getEdges() {
        return edges.keySet().stream().map(key -> edges.get(key)).collect(Collectors.toList());
    }
}
