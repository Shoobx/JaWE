package org.enhydra.jawe.components.graph;

import java.awt.Point;

import org.jgraph.graph.EdgeView;

/**
 * Interface for representing a view for a JGraph's Transition objects.
 *
 * @author Sasa Bojanic
 */
public abstract class GraphTransitionViewInterface extends EdgeView {

   public GraphTransitionViewInterface(Object cell) {
      super(cell);
   }
   
   public abstract void addPoint(Graph g,Point p);

   public abstract void removePoint(Graph g,Point p);
   
}
