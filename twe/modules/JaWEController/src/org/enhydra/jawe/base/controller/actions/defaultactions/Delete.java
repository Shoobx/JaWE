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

/**
 * Miroslav Popov, Sep 19, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.controller.actions.defaultactions;

import java.awt.event.ActionEvent;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.shark.xpdl.XMLElement;

/**
 * @author Miroslav Popov
 *
 */
public class Delete extends ActionBase {
   
   public Delete (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      JaWEController jc = (JaWEController)jawecomponent;
      
      if (jc.getSelectionManager().canDelete())
         setEnabled(true);
      else
         setEnabled(false);
   }
   
   public void actionPerformed(ActionEvent e) {
       JaWEController jc=JaWEManager.getInstance().getJaWEController();
       List sel=jc.getSelectionManager().getSelectedElements();
       XMLElement firstSelected = jc.getSelectionManager().getSelectedElement();

       if (jc.confirmDelete(sel,firstSelected)) {
          jc.getEdit().delete();
       }

   }
   
}
