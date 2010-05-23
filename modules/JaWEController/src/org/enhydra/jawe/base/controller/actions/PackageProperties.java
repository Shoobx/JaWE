package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.editor.XPDLElementEditor;


/**
 * Class that realizes <B>package properties</B> action.
 */
public class PackageProperties extends ActionBase {

   public PackageProperties (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {     
      if (getPackage() != null)
         setEnabled(true);
      else 
         setEnabled(false);  
   }
   
   public void actionPerformed(ActionEvent e) {
      if (getPackage()==null) return;
      JaWEController jc = (JaWEController)jawecomponent;
      jc.getSelectionManager().setSelection(getPackage(), false);
      XPDLElementEditor ed=JaWEManager.getInstance().getXPDLElementEditor();
      ed.editXPDLElement(getPackage());
   }
}
