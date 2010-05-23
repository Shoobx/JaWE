package org.enhydra.jawe.base.controller.actions.defaultactions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.UndoHistoryManager;
import org.enhydra.jawe.base.controller.JaWEController;

/**
 * Selects "next" selected graph.
 * @author Sasa Bojanic
 */
public class Redo extends ActionBase {

   public Redo (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
      UndoHistoryManager uhm=((JaWEController)jawecomponent).getUndoHistoryManager();
      setEnabled(uhm!=null && uhm.canRedo());
   }
   
   public void actionPerformed(ActionEvent e) {
      ((JaWEController)jawecomponent).redo();      
   }
}
