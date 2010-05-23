package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.GraphEAConstants;

/**
* @author Sasa Bojanic
*/
public class SetTransitionStyleNoRoutingBezier extends SetTransitionStyle {

   public SetTransitionStyleNoRoutingBezier (JaWEComponent jawecomponent) {
      super(jawecomponent,GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_BEZIER);
   }
  
   public void actionPerformed(ActionEvent e) {
      super.actionPerformed(e);
   }
}
