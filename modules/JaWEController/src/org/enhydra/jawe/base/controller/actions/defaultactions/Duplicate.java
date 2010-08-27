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
public class Duplicate extends ActionBase {
   
   public Duplicate (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      JaWEController jc = (JaWEController)jawecomponent;      
      
      if (jc.getSelectionManager().canDuplicate())
         setEnabled(true);
      else
         setEnabled(false);      
   }
   
   public void actionPerformed(ActionEvent e) {   
      JaWEController jc = (JaWEController)jawecomponent;
      
      jc.getEdit().duplicate();
   }
}
