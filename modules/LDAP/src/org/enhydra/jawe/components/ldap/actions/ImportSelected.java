package org.enhydra.jawe.components.ldap.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.ldap.LDAPController;
import org.enhydra.shark.xpdl.elements.Package;

/**
 * Imports selected entries as Participants into XPDL.
 * 
 * @author Sasa Bojanic
 */
public class ImportSelected extends ActionBase {

   public ImportSelected(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      Package mainPkg=JaWEManager.getInstance().getJaWEController().getMainPackage();
      Package workingPkg=JaWEManager.getInstance().getJaWEController().getSelectionManager().getWorkingPKG();
      if (mainPkg!=null && mainPkg==workingPkg) {
         setEnabled(true); 
      } else {
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {      
      LDAPController controller=(LDAPController)jawecomponent;
      controller.importSelectedEntries();
   }   
}
