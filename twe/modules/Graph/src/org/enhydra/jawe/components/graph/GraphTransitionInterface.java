package org.enhydra.jawe.components.graph;

import org.jgraph.graph.DefaultEdge;

/**
 * Interface for defining Transition graph object.
 *
 * @author Sasa Bojanic   
 */
public abstract class GraphTransitionInterface extends DefaultEdge implements WorkflowElement {

   /**
    * Returns source activity.
    */
   public abstract GraphActivityInterface getSourceActivity ();

   /**
    * Returns target activity.
    */
   public abstract GraphActivityInterface getTargetActivity ();
   
   public abstract boolean hasCondition ();

}
