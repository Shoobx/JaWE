package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Package;


/**
 * Removes external package from the system.
 * @author Sasa Bojanic
 */
public class PackageRemoveExternalPackage extends ActionBase {

   public PackageRemoveExternalPackage (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {  
      JaWEController jc = (JaWEController)jawecomponent;
      Package mainPkg=jc.getMainPackage();
      XMLElement firstSelected=null;
      if (mainPkg != null && (firstSelected=jc.getSelectionManager().getSelectedElement()) instanceof Package) {                  
         String epId=((Package)firstSelected).getId();
         boolean en=mainPkg.getExternalPackageIds().contains(epId);
         setEnabled(en);
      } else { 
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc = (JaWEController)jawecomponent;            
      Package mainPkg=jc.getMainPackage();
      if (mainPkg==null) return;
      jc.removeExternalPackage();
   }
}
