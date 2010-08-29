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
 * Miroslav Popov, Sep 27, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.XPDLConstants;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.BlockActivity;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * @author Miroslav Popov
 *
 */
public class DescendInto extends ActionBase {

   public DescendInto (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();

      boolean isEnabled = false;
      if (jc.getSelectionManager().getSelectedElements().size() == 1) {
         XMLElement el = (XMLElement) jc.getSelectionManager().getSelectedElements().toArray()[0];
         if (el instanceof Activity) {
            Activity a = (Activity) el;

            XMLElement implementation = null;
            if (a.getActivityType() == XPDLConstants.ACTIVITY_TYPE_SUBFLOW) {
               implementation = XMLUtil.getSubflowProcess(JaWEManager.getInstance().getXPDLHandler(), a);
            } else {
               implementation = XMLUtil.getBlockActivitySet(a);               
            }

            if (implementation != null) isEnabled = true;
         }
      }

      setEnabled(isEnabled);
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      GraphController gc = (GraphController)jawecomponent;
      
      XMLElement el = (XMLElement)jc.getSelectionManager().getSelectedElements().toArray()[0];
      
      if (el instanceof Activity) {
         Activity a = (Activity) el;
         
         if (a.getActivityType() == XPDLConstants.ACTIVITY_TYPE_SUBFLOW) {
            WorkflowProcess wp = XMLUtil.getSubflowProcess(JaWEManager.getInstance().getXPDLHandler(), a);
            gc.selectGraphForElement(wp);
         } else {
            BlockActivity blk = a.getActivityTypes().getBlockActivity();
            String blockId = blk.getBlockId();
            // check if the activity set exists
            ActivitySet as = XMLUtil.getWorkflowProcess(a).getActivitySet(blockId);
            gc.selectGraphForElement(as);
         }
      }   
   }
}
