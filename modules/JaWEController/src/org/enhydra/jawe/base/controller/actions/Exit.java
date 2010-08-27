package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;

/**
 * Class that realizes <B>exit</B> action.
 * Really lame implementation of an exit command.
 * @author Sasa Bojanic
 */
public class Exit extends ActionBase {

   public Exit (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc=JaWEManager.getInstance().getJaWEController();
      if (jc.tryToClosePackage(jc.getMainPackageId(), false)) {
         System.exit(0);
      }
   }
}
