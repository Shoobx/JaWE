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

package org.enhydra.jawe.components.propertiespanel.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.components.propertiespanel.PropertiesPanelComponent;

/**
 * @author Zoran Milakovic
 */
public class NextPanel extends ActionBase {

   protected InlinePanel ipc;
   
   public NextPanel (JaWEComponent jawecomponent) {
      super(jawecomponent);
      this.ipc=(InlinePanel)((PropertiesPanelComponent)jawecomponent).getView();
      enabled=false;      
   }

   public void enableDisableAction() {      
      setEnabled(ipc.getHistoryManager()!=null && ipc.getHistoryManager().canGoForward());
   }
   
   public void actionPerformed(ActionEvent e) {
      ipc.displayNextElement();      
   }
}
