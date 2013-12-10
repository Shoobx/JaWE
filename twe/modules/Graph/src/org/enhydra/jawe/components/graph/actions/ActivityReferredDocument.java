/**
* Together Workflow Editor
* Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
* 
* This program is free software: you can redistribute it and/or modify 
* it under the terms of the GNU General Public License as published by 
* the Free Software Foundation, either version 3 of the License, or 
* (at your option) any later version. 
*
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
* GNU General Public License for more details. 
*
* You should have received a copy of the GNU General Public License 
* along with this program. If not, see http://www.gnu.org/licenses
*/

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
