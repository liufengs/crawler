package ca.credits.base.diagram;

import lombok.Builder;
import lombok.Data;

/**
 * Created by chenwen on 16/9/1.
 * this class is the edge of event stream
 */
@Data
@Builder
public class Edge {
    /**
     * the edge id
     */
    private String id;

    /**
     * the source node
     */
    private AbstractNode source;

    /**
     * the target node
     */
    private AbstractNode target;

    /**
     * destroy this
     */
    public void destroy(){
        try {
            this.source.removeChild(target);
            this.target.removeParent(source);
        }catch (Exception e){
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge node = (Edge) o;

        return id != null ? id.equals(node.id) : node.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
