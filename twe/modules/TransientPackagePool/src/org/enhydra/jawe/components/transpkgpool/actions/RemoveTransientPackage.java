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

package org.enhydra.jawe.components.transpkgpool.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Package;


/**
 * Removes transient package from the system.
 * @author Sasa Bojanic
 */
public class RemoveTransientPackage extends ActionBase {

   public RemoveTransientPackage (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {  
      JaWEController jc = JaWEManager.getInstance().getJaWEController();            
      XMLElement firstSelected=jc.getSelectionManager().getSelectedElement();
      if (firstSelected!=null) {
         Package selPkg=XMLUtil.getPackage(firstSelected);
         if(selPkg!=null && selPkg.isTransient()) {
            setEnabled(true);
         } else {
            setEnabled(false);
         }
      } else { 
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();            
      jc.removeTransientPackage();
   }
}
