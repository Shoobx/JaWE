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
