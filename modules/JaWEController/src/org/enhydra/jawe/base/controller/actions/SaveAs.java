package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;

/**
 * Class that realizes <B>save as</B> action.
 * @author Sasa Bojanic
 */
public class SaveAs extends Save {

   public SaveAs (JaWEComponent jawecomponent) {
      super(jawecomponent,"SaveAs");
   }

   public void actionPerformed(ActionEvent e) {
      super.actionPerformed(e);
   }
   
   public void enableDisableAction() {
      setEnabled(JaWEManager.getInstance().getJaWEController().isSaveEnabled(true));
   }
   
}
