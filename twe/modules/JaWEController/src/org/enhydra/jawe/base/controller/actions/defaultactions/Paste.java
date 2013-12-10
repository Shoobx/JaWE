/**
 * Miroslav Popov, Sep 1, 2005
 */
package org.enhydra.jawe.base.controller.actions.defaultactions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.controller.JaWEController;

/**
 * @author Miroslav Popov
 *
 */
public class Paste extends ActionBase {
   
   public Paste (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {  
      JaWEController jc = (JaWEController)jawecomponent;
      
      if (jc.getSelectionManager().canPaste())
         setEnabled(true);
      else
         setEnabled(false);
   }
   
   public void actionPerformed(ActionEvent e) {
      ((JaWEController)jawecomponent).getEdit().paste();
   }
}
