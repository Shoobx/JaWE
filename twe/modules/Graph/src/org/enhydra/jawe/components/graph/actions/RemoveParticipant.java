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
 * Miroslav Popov, Sep 21, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Removes selected participant from graph (does not delete it from the model),
 * and deletes all of the elements it contains from the XPDL model.
 * @author Miroslav Popov
 * @author Sasa Bojanic
 */
public class RemoveParticipant extends ActionBase {

   public RemoveParticipant (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      boolean en=false;
      if (gc.getSelectedGraph() != null) {
         if (!gc.getSelectedGraph().getXPDLObject().isReadOnly()) {
            en=true;
         }
      }
      setEnabled(en);
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gc = (GraphController)jawecomponent;
      gc.getSelectedGraph().getGraphManager().removeCells(gc.getSelectedGraph().getSelectionCells());
      JaWEManager.getInstance().getJaWEController().getSelectionManager().setSelection(gc.getSelectedGraph().getXPDLObject(), true);                              
   }   
}
