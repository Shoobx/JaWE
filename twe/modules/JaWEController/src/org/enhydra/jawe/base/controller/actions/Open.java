package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;

/**
 * Class that realizes <B>open</B> action.
 * @author Sasa Bojanic
 */
public class Open extends ActionBase {

   public Open (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc=JaWEManager.getInstance().getJaWEController();
      String fn=jc.getPackageFilename(jc.getMainPackageId());
      if (jc.tryToClosePackage(jc.getMainPackageId(), false)) {
         String name = jc.openDialog(jc.getSettings().getLanguageDependentString("Open" + BarFactory.LABEL_POSTFIX),fn);
         if (name==null) return;
         jc.openPackageFromFile(name);
      }
      
   }

}
