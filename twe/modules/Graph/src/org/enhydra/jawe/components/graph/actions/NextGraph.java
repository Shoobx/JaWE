package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Selects "next" selected graph.
 * @author Sasa Bojanic
 */
public class NextGraph extends ActionBase {

   public NextGraph (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController) jawecomponent;
      setEnabled(gc.getHistoryManager()!=null && gc.getHistoryManager().canGoForward());
   }
   
   public void actionPerformed(ActionEvent e) {
      ((GraphController)jawecomponent).displayNextGraph();
   }
}
