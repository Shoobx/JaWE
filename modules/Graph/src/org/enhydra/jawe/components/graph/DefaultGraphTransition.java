/**
* Together Workflow Editor
* Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
* 
* This program is free software: you can redistribute it and/or modify 
* it under the terms of the GNU General Public License as published by 
* the Free Software Foundation, either version 3 of the License, or 
* (at your option) any later version. 
*
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
* GNU General Public License for more details. 
*
* You should have received a copy of the GNU General Public License 
* along with this program. If not, see http://www.gnu.org/licenses
*/

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

