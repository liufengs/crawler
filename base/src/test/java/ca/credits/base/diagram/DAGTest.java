package ca.credits.base.diagram;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenwen on 16/9/1.
 */
@Slf4j
public class DAGTest {

    @Test
    public void test()throws Exception{
        AbstractNode abstractNode1 = DefaultEventNode.create("1");
        AbstractNode abstractNode2 = DefaultEventNode.create("2");
        AbstractNode abstractNode3 = DefaultEventNode.create("3");
        AbstractNode abstractNode4 = DefaultEventNode.create("4");
        AbstractNode abstractNode5 = DefaultEventNode.create("5");
        AbstractNode abstractNode6 = DefaultEventNode.create("6");

        Edge edge1 = new Edge("1", abstractNode1, abstractNode2);
        Edge edge2 = new Edge("2", abstractNode1, abstractNode3);

        Edge edge3 = new Edge("3", abstractNode2, abstractNode4);
        Edge edge4 = new Edge("4", abstractNode2, abstractNode5);

        Edge edge5 = new Edge("5", abstractNode3, abstractNode6);
        Edge edge6 = new Edge("6", abstractNode4, abstractNode6);
        Edge edge7 = new Edge("7", abstractNode5, abstractNode6);

        List<Edge> edges = new ArrayList<Edge>(){{
            add(edge1);
            add(edge2);
            add(edge3);
            add(edge4);
            add(edge5);
            add(edge6);
            add(edge7);
        }};

        DAG dag = DAG.create();

        edges.parallelStream().forEach(key -> {
            try {
                dag.addEdges(key);
            }catch (Exception e){

            }
        });

        AbstractNode task = DefaultTaskNode.create("1",dag);

        log.info("");

        log.info(task.show());
    }
}