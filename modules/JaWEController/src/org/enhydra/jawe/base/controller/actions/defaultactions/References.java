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
public class References extends ActionBase {
   
   public References (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      JaWEController jc = (JaWEController)jawecomponent;      
      
      if (jc.getSelectionManager().canGetReferences())
         setEnabled(true);
      else
         setEnabled(false);            
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc = (JaWEController)jawecomponent;      
      jc.getEdit().references();
   }
}
