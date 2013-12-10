package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.controller.JaWEFrame;

/**
 * @author Sasa Bojanic
 */
public class PackageReferredDocument extends ActionBase {

   public PackageReferredDocument(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
      if (getPackage() != null) {
         String doc=getPackage().getPackageHeader().getDocumentation();
         if (doc.trim().length()>0) {
            setEnabled(true);            
         } else {
            setEnabled(false);
         }
      } else { 
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {
      if (getPackage()==null) return;
      String doc=getPackage().getPackageHeader().getDocumentation();
      boolean ok=Utils.showExternalDocument(doc);
      if (!ok) {
         JaWEController jc=JaWEManager.getInstance().getJaWEController();
          JOptionPane.showMessageDialog(jc.getJaWEFrame(),
          doc+": "+
          ResourceManager.getLanguageDependentString("InformationFileNotReadable"),
          jc.getAppTitle(), JOptionPane.INFORMATION_MESSAGE);         
      }
   }
   
}
