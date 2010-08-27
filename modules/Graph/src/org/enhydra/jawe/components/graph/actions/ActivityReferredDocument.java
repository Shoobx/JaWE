package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.controller.JaWEFrame;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Activity;

/**
 * @author Sasa Bojanic
 */
public class ActivityReferredDocument extends ActionBase {

   public ActivityReferredDocument(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
      JaWEController jc = JaWEManager.getInstance().getJaWEController();

      boolean isEnabled = false;
      if (jc.getSelectionManager().getSelectedElements().size() == 1) {
         XMLElement el = jc.getSelectionManager().getSelectedElement();
         if (el instanceof Activity) {
            Activity a = (Activity) el;

            String doc=a.getDocumentation();
            if (doc.trim().length()>0) isEnabled = true;
         }
      }

      setEnabled(isEnabled);
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      XMLElement el = jc.getSelectionManager().getSelectedElement();
      if (el instanceof Activity) {
         Activity a = (Activity) el;

         String doc=a.getDocumentation();
         boolean ok=Utils.showExternalDocument(doc);
         if (!ok) {
            JOptionPane.showMessageDialog(jc.getJaWEFrame(), doc + ": "+ 
                  ResourceManager.getLanguageDependentString("InformationFileNotReadable"), 
                  jc.getAppTitle(),
                  JOptionPane.INFORMATION_MESSAGE);         
         }
      }
   }
   
}
