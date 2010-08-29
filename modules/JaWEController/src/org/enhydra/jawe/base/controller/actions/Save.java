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
import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.shark.xpdl.StandardPackageValidator;
import org.enhydra.shark.xpdl.elements.Package;

/**
 * Class that realizes <B>save</B> action.
 * @author Sasa Bojanic
 */
public class Save extends ActionBase {

   private String myName;
   public Save (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public Save (JaWEComponent jawecomponent,String name) {
      super(jawecomponent, name);
      this.myName=name;
   }

   public void enableDisableAction() {
      setEnabled(JaWEManager.getInstance().getJaWEController().isSaveEnabled(false));
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc=JaWEManager.getInstance().getJaWEController();
      
      boolean save = true;
      boolean allowInvalidPackageSaving = jc.getControllerSettings().allowInvalidPackageSaving() && !"Released".equalsIgnoreCase(jc.getMainPackage().getRedefinableHeader().getPublicationStatus());
      boolean isModelOK = false;
      
      if (!allowInvalidPackageSaving) {
         StandardPackageValidator xpdlValidator=JaWEManager.getInstance().getXPDLValidator();
         xpdlValidator.init(            
               JaWEManager.getInstance().getXPDLHandler(), 
               jc.getMainPackage(), 
               false, 
               jc.getControllerSettings().getEncoding(),
               JaWEManager.getInstance().getStartingLocale());

         isModelOK = jc.checkValidity(jc.getMainPackage(), false).size()==0;
         if (!isModelOK) {
            String msg=jc.getSettings().getLanguageDependentString("ErrorCannotSaveIncorrectPackage");
            jc.message(msg,JOptionPane.ERROR_MESSAGE);
            save = false;
         }
      }
      
      if (save) {
         String oldFilename=jc.getPackageFilename(jc.getMainPackageId());
         String newFilename=null;
         Package pkg=jc.getMainPackage();
         if (oldFilename==null || myName!=null) {
            newFilename=jc.saveDialog(
                  jc.getSettings().getLanguageDependentString("SaveAs" + BarFactory.LABEL_POSTFIX),0,
                  pkg.getId());            
         } else {
            newFilename=oldFilename;
         }
         if (newFilename!=null) {
            jc.savePackage(pkg.getId(), newFilename);
         }
      }
   }

}
