package org.enhydra.jawe.components.graph;

import java.awt.Point;
import java.util.Set;

import org.jgraph.graph.DefaultGraphCell;

/**
 * Interface for defining Activity graph object.
 *
 * @author Sasa Bojanic
 */
public abstract class GraphActivityInterface extends DefaultGraphCell implements WorkflowElement, Linkable {

   /**
    * Gets the port associated with this activity.
    */
   public abstract GraphPortInterface getPort ();

   /**
    * Gets all activities that reference this one.
    */
   public abstract Set getReferencingActivities ();

   /**
    * Gets all activities that this activity references.
    */
   public abstract Set getReferencedActivities ();

   public abstract Point getOffset ();
   
   
}
