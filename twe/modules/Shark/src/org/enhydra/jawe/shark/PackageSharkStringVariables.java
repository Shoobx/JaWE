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

package org.enhydra.jawe.shark;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.editor.XPDLElementEditor;


/**
 * Class that implements action to display shark config strings on package level.
 */
public class PackageSharkStringVariables extends ActionBase {

   public PackageSharkStringVariables (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {  
      if (getPackage() != null)
         setEnabled(true);
      else 
         setEnabled(false);
   }
   
   public void actionPerformed(ActionEvent e) {
      if (getPackage()==null) return;
      JaWEController jc = (JaWEController)jawecomponent;
      SharkStringExtendedAttributesWrapper eaw = new SharkStringExtendedAttributesWrapper(getPackage().getExtendedAttributes());
      jc.getSelectionManager().setSelection(eaw, false);
      XPDLElementEditor ed=JaWEManager.getInstance().getXPDLElementEditor();
      ed.editXPDLElement(eaw);
   }
}
