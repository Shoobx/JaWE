package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.GraphEAConstants;

/**
* @author Sasa Bojanic
*/
public class SetTransitionStyleSimpleRoutingSpline extends SetTransitionStyle {

   public SetTransitionStyleSimpleRoutingSpline (JaWEComponent jawecomponent) {
      super(jawecomponent,GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_SIMPLE_ROUTING_SPLINE);
   }
  
   public void actionPerformed(ActionEvent e) {
      super.actionPerformed(e);
   }

}
