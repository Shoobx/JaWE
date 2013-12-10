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

import javax.swing.JOptionPane;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.editor.NewStandardXPDLElementEditor;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.shark.xpdl.XMLComplexChoice;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;

/**
 * @author Sasa Bojanic
 */
public class DisplayParentElementPanel extends ActionBase {

   protected InlinePanel ipc;
	
   public DisplayParentElementPanel (JaWEComponent jawecomponent) {
      super(jawecomponent);
      this.ipc=(InlinePanel)((NewStandardXPDLElementEditor)jawecomponent).getView();
      setEnabled(false);
   }

   public void enableDisableAction() {
      XMLElement el=ipc.getActiveElement();
      if (el!=null && el.getParent()!=null) {
         setEnabled(true);
         return;
      } 
      
      setEnabled(false);      
   }
   
   public void actionPerformed(ActionEvent e) {
      XMLElement el=ipc.getActiveElement();
      XMLElement parent = null;
      if (el != null) {
         parent = el.getParent();
         if (parent != null) {
            XMLElement choice = null;
            while ((choice = XMLUtil.getParentElementByAssignableType(XMLComplexChoice.class, parent)) != null) {
               parent = choice.getParent();
            }
         }
      }

      if (el != null) {
         if (ipc.isModified()) {
            int sw=ipc.showModifiedWarning();
            if( sw == JOptionPane.CANCEL_OPTION || (sw==JOptionPane.YES_OPTION && ipc.isModified())) {
               return;
            }
         }
         ((NewStandardXPDLElementEditor)jawecomponent).editXPDLElement(parent);
      }
   }
}
