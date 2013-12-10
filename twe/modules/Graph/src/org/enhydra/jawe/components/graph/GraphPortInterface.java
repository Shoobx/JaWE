package org.enhydra.jawe.components.graph;

import org.jgraph.graph.DefaultPort;

/**
 * Interface for defining graph port object.
 *
 * @author Sasa Bojanic   
 */
public abstract class GraphPortInterface extends DefaultPort {
   
   public GraphPortInterface (String name) {
      super(name);
   }
   
   public abstract GraphActivityInterface getActivity ();
   
   public abstract String getType ();
   
}
