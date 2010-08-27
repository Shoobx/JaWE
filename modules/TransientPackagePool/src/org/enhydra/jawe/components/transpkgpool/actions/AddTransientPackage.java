package org.enhydra.jawe.components.transpkgpool.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;


/**
 * Adds transient package into the system.
 * @author Sasa Bojanic
 */
public class AddTransientPackage extends ActionBase {

   public AddTransientPackage (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {  
      setEnabled(true);
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();            
      jc.addTransientPackage();
   }
      
}
