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

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.shark.xpdl.elements.Package;


/**
 * Adds external package into the system.
 * @author Sasa Bojanic
 */
public class PackageAddExternalPackage extends ActionBase {

   public PackageAddExternalPackage (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {  
      JaWEController jc = (JaWEController)jawecomponent;
      if (jc.getMainPackage() != null && jc.getPackageFilename(jc.getMainPackageId())!=null)
         setEnabled(true);
      else 
         setEnabled(false);  
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc = (JaWEController)jawecomponent;            
      Package mainPkg=jc.getMainPackage();
      if (mainPkg==null) return;
      jc.addExternalPackage();
   }
      
}
