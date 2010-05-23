package org.enhydra.jawe.components.graph;

/**
* Defines the requirements for an object that
* represents a cell that should be linked with
* other cells.
*/

public interface Linkable {
   /**
   * Returns <code>true</code> if cell that implements it is a valid source for link.
   */
   boolean acceptsSource();

   /**
   * Returns <code>true</code> if cell that implements it is a valid target for link.
   */
   boolean acceptsTarget();

}

