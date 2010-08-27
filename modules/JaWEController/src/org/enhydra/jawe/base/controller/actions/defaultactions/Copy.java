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
public class Copy extends ActionBase {
   
   public Copy (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }
   
   public void enableDisableAction() {
      JaWEController jc = (JaWEController)jawecomponent;
      
      if (jc.getSelectionManager().canCopy())
         setEnabled(true);
      else
         setEnabled(false);
   }
   
   public void actionPerformed(ActionEvent e) {
      ((JaWEController)jawecomponent).getEdit().copy();
   }
}
