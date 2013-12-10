package org.enhydra.jawe.components.transpkgpool.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.Package;


/**
 * Removes transient package from the system.
 * @author Sasa Bojanic
 */
public class RemoveTransientPackage extends ActionBase {

   public RemoveTransientPackage (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {  
      JaWEController jc = JaWEManager.getInstance().getJaWEController();            
      XMLElement firstSelected=jc.getSelectionManager().getSelectedElement();
      if (firstSelected!=null) {
         Package selPkg=XMLUtil.getPackage(firstSelected);
         if(selPkg!=null && selPkg.isTransient()) {
            setEnabled(true);
         } else {
            setEnabled(false);
         }
      } else { 
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();            
      jc.removeTransientPackage();
   }
}
