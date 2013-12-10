package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Removes start and end bubbles into graph.
 * @author Sasa Bojanic
 */
public class RemoveStartAndEndBubbles extends ActionBase {

   public RemoveStartAndEndBubbles (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      setEnabled(gc.getSelectedGraph()!=null && gc.getGraphSettings().shouldUseBubbles() && !gc.getSelectedGraph().getXPDLObject().isReadOnly());
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gc = (GraphController)jawecomponent;
      gc.getSelectedGraph().getGraphManager().removeStartEndBubbles();                              
   }   
}
