package org.enhydra.jawe.components.graph;

import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.tooltip.TooltipGenerator;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.elements.Transition;
import org.jgraph.graph.DefaultPort;

/**
 * Used to define Transition object in the graph.
 * @author Sasa Bojanic
 */
public class DefaultGraphTransition extends GraphTransitionInterface {

   /**
    * Creates transition.
    */
   public DefaultGraphTransition (Transition tra) {
      super();
      setUserObject(tra);
      
   }


   /**
    * Returns source activity.
    */
   public GraphActivityInterface getSourceActivity () {
      return (GraphActivityInterface)((DefaultPort)source).getParent();
   }

   /**
    * Returns target activity.
    */
   public GraphActivityInterface getTargetActivity () {
      return (GraphActivityInterface)((DefaultPort)target).getParent();
   }

   public XMLComplexElement getPropertyObject () {
      if (userObject instanceof XMLComplexElement) {
         return (XMLComplexElement)userObject;
      } 
      return null;         
   }

   /**
    * Gets a tooltip text for transition.
    */
   public String getTooltip () {
      TooltipGenerator ttg=JaWEManager.getInstance().getTooltipGenerator();
      if (userObject!=null && ttg!=null) {
         return ttg.getTooltip(getPropertyObject());
      }
      return "";
   }

   /**
    * Returns an empty string.
    */
   public String toString () {
//      org.enhydra.shark.xpdl.elements.Transition tr=
//         (org.enhydra.shark.xpdl.elements.Transition)userObject;
//      return tr.getCondition().toValue();
      return "";
   }

   //HM: enable Transition-copy/paste
   protected Object cloneUserObject() {
      if (userObject instanceof Transition) {
         return ((Transition)userObject).clone();
      } 
      return null;      
   }

   public boolean hasCondition () {
      return !getCondition().equals("");
   }

   public String getConditionType () {
      if (!(userObject instanceof Transition)) return "";
      Transition tr=(Transition)userObject;
      return tr.getCondition().getType();
   }

   public String getCondition () {
      if (!(userObject instanceof Transition)) return "";
      Transition tr=(Transition)userObject;
      return tr.getCondition().toValue();
   }

   public String getType () {
      if (userObject!=null) {
         return JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType((Transition) userObject).getTypeId();
      } 
      
      return JaWEConstants.TRANSITION_TYPE_UNCONDITIONAL;      
   }
}

