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

package org.enhydra.jawe.base.editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.editor.NewStandardXPDLElementEditor;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jxpdl.XMLElement;

/**
 * @author Zoran Milakovic
 */
public class ApplyChanges extends ActionBase {

   protected InlinePanel ipc;
	
   public ApplyChanges (JaWEComponent jawecomponent) {
      super(jawecomponent);
      this.ipc=(InlinePanel)((NewStandardXPDLElementEditor)jawecomponent).getView();
      enabled=false;
      
       jawecomponent.getView().getDisplay().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
             KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
             Utils.getUnqualifiedClassName(this.getClass()));
       
       jawecomponent.getView().getDisplay().getActionMap().put(Utils.getUnqualifiedClassName(this.getClass()), this);
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