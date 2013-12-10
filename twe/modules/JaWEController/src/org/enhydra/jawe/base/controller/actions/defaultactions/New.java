/**
 * Miroslav Popov, Sep 26, 2005
 * miroslav.popov@gmail.com
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
public class New extends ActionBase {
   
   public New(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      JaWEController jc = (JaWEController)jawecomponent;      
      
      if (jc.getSelectionManager().canInsertNew())
         setEnabled(true);
      else
         setEnabled(false);            
   }
   
   public void actionPerformed(ActionEvent e) {      
   }
}
