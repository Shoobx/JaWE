package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Selects previously selected graph.
 * @author Sasa Bojanic
 */
public class PreviousGraph extends ActionBase {

   public PreviousGraph (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
      GraphController gc=(GraphController)jawecomponent;
      setEnabled(gc.getHistoryManager()!=null && gc.getHistoryManager().canGoBack());
   }
   
   public void actionPerformed(ActionEvent e) {
      ((GraphController)jawecomponent).displayPreviousGraph();      
   }
}
