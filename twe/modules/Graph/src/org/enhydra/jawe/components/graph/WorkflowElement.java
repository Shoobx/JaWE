package org.enhydra.jawe.components.graph;

import org.enhydra.shark.xpdl.XMLComplexElement;

public interface WorkflowElement {

   /**
   * Gets a property object (XML schema Element).
   */
   public XMLComplexElement getPropertyObject ();

   /**
   * Sets an userObject property which name is given in parameter what to
   * a value given in a parameter value.
   */

   /**
   * Gets a tooltip text for element.
   */
   public String getTooltip ();

   public String getType ();
}
