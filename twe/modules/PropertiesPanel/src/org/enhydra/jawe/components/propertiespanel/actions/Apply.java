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

package org.enhydra.jawe.components.propertiespanel.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.components.propertiespanel.PropertiesPanelComponent;
import org.enhydra.jxpdl.XMLElement;

/**
 * @author Zoran Milakovic
 */
public class Apply extends ActionBase {

   protected InlinePanel ipc;
	
   public Apply (JaWEComponent jawecomponent) {
      super(jawecomponent);
      this.ipc=(InlinePanel)((PropertiesPanelComponent)jawecomponent).getView();
      enabled=false;
   }

   public void enableDisableAction() {
      if (ipc.isModified()) {
         setEnabled(true);
      } else {
         setEnabled(false);
      }
   }

   public void actionPerformed(ActionEvent e) {
      if (!ipc.canApplyChanges()) return;
      XMLElement el=ipc.getActiveElement();

      jawecomponent.setUpdateInProgress(true);
      JaWEManager.getInstance().getJaWEController().startUndouableChange();
      ipc.apply();
      List toSelect = new ArrayList();
      XMLElement toSel = el;
      if (toSel!=null) {
         toSelect.add(toSel);
      }
      JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
      jawecomponent.setUpdateInProgress(false);

      ipc.validateElement(el);      
      
      ipc.setModified(false);
   }
   
}