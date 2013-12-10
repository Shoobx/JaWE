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
