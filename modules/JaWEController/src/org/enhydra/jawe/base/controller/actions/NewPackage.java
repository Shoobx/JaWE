package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.NewActionBase;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.controller.JaWETypeChoiceButton;
import org.enhydra.shark.xpdl.elements.Package;

/**
 * Class that realizes <B>new</B> action.
 * 
 * @author Sasa Bojanic
 */
public class NewPackage extends NewActionBase {

   public NewPackage(JaWEComponent jawecomponent) {
      super(jawecomponent, Package.class, null);
   }

   public void enableDisableAction() {
   }

   public void actionPerformed(ActionEvent e) {
      if (!(e.getSource() instanceof JaWETypeChoiceButton)) {
         JaWEController jc = JaWEManager.getInstance().getJaWEController();
         if (jc.tryToClosePackage(jc.getMainPackageId(), false)) {
            jc.newPackage(jc.getJaWETypes().getDefaultType(Package.class,null));
         }
      }
   }

}
