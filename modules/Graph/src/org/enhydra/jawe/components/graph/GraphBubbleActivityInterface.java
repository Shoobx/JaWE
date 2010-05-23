package org.enhydra.jawe.components.graph;


/**
 * Interface for defining Activity graph object.
 *
 * @author Sasa Bojanic
 */
public abstract class GraphBubbleActivityInterface extends GraphActivityInterface {

   /**
    * Gets description of bubble.
    */
   public abstract StartEndDescription getStartEndDescription();

   public abstract boolean isStart ();
   
}
