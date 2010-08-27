package org.enhydra.jawe.components.graph;

import java.awt.Dimension;

import org.jgraph.graph.PortView;

/**
 * Interface for representing a view for a JGraph's port objects.
 *
 * @author Sasa Bojanic   
 */
public abstract class GraphPortViewInterface extends PortView {
   
   public GraphPortViewInterface(Object cell) {
      super(cell);
   } 
   
   public abstract void setPortSize (Dimension d);

   public abstract Dimension getPortsSize ();
 
   public abstract GraphActivityInterface getGraphActivity ();
}
