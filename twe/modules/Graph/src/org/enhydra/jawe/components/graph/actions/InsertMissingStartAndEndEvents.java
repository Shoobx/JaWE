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

package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Inserts missing Start and End events into the model and graph.
 * @author Sasa Bojanic
 */
public class InsertMissingStartAndEndEvents extends ActionBase {

   public InsertMissingStartAndEndEvents (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      setEnabled(gc.getSelectedGraph()!=null && !gc.getSelectedGraph().getXPDLObject().isReadOnly());
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gc = (GraphController)jawecomponent;
      if(gc.getSelectedGraph()!=null) {
         gc.setUpdateInProgress(true);
         JaWEManager.getInstance().getJaWEController().startUndouableChange();
         gc.getSelectedGraph().getGraphManager().insertMissingStartAndEndEvents();
         List toSelect=new ArrayList();
         toSelect.add(gc.getSelectedGraph().getGraphManager().getXPDLOwner());
         JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
         gc.setUpdateInProgress(false);                  
      }
   }   
}
