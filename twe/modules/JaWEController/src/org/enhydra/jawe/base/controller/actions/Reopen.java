package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;

/**
 * Class that realizes <B>reopen</B> action.
 * @author Sasa Bojanic
 */
public class Reopen extends ActionBase {

   public Reopen (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
      JaWEController jc = (JaWEController)jawecomponent;
      boolean en=false;
      if (jc.getMainPackage() != null) {
         String name = jc.getPackageFilename(jc.getMainPackageId());
         if (name!=null && jc.isPackageModified(jc.getMainPackageId())) {
            en=true;
         }
      }
      setEnabled(en);
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc=JaWEManager.getInstance().getJaWEController();
      String name = jc.getPackageFilename(jc.getMainPackageId());
      if (name==null) {
         String msg=jc.getSettings().getLanguageDependentString("WarningCannotReopenXPDL");
         jc.message(msg,JOptionPane.WARNING_MESSAGE);
         return;
      }
      if (jc.tryToClosePackage(jc.getMainPackageId(), false)) {
         jc.openPackageFromFile(name);
      }
   }

}
