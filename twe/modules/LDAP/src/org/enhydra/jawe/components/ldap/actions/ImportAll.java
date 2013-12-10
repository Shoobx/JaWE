package org.enhydra.jawe.components.ldap.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.ldap.LDAPController;
import org.enhydra.shark.xpdl.elements.Package;

/**
 * Imports all entries as Participants into XPDL.
 * 
 * @author Sasa Bojanic
 */
public class ImportAll extends ActionBase {

   public ImportAll(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      LDAPController controller=(LDAPController)jawecomponent;
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
      controller.importAllEntries();
   }   
}
