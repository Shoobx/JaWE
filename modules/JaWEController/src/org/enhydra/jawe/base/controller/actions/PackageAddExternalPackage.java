package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.shark.xpdl.elements.Package;


/**
 * Adds external package into the system.
 * @author Sasa Bojanic
 */
public class PackageAddExternalPackage extends ActionBase {

   public PackageAddExternalPackage (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {  
      JaWEController jc = (JaWEController)jawecomponent;
      if (jc.getMainPackage() != null && jc.getPackageFilename(jc.getMainPackageId())!=null)
         setEnabled(true);
      else 
         setEnabled(false);  
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc = (JaWEController)jawecomponent;            
      Package mainPkg=jc.getMainPackage();
      if (mainPkg==null) return;
      jc.addExternalPackage();
   }
      
}
