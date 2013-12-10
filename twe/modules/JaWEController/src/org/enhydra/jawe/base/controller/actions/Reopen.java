/**
* Together Workflow Editor
* Copyright (C) 2011 Together Teamsolutions Co., Ltd. 
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
