package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.controller.JaWEController;

/**
 * Class that implements <B>package check validity</B> action.
 */
public class PackageCheckValidity extends ActionBase {

   public PackageCheckValidity (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
      JaWEController jc = (JaWEController)jawecomponent;
      if (jc.getMainPackage() != null)
         setEnabled(true);
      else 
         setEnabled(false);  
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc = (JaWEController)jawecomponent;
      jc.checkValidity(jc.getMainPackage(), true, true, false);
   }
}
