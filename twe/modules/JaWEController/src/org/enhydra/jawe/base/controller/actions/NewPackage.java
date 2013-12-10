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
