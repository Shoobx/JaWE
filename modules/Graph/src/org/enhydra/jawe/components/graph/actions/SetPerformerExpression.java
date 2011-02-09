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

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.ExpressionParticipantEditor;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.WorkflowElement;
import org.enhydra.jxpdl.elements.Lane;

/**
 * Sets the performer expression for all the activities contained inside
 * selected CommonExpressionParticipant.
 * @author Sasa Bojanic
 */
public class SetPerformerExpression extends ActionBase {

   public SetPerformerExpression (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      boolean en=false;
      if (gc.getSelectedGraph() != null) {
         Graph g=gc.getSelectedGraph();
         Object[] scells=g.getSelectionCells();
         if (!g.getXPDLObject().isReadOnly() && scells!=null && scells.length==1 && ((WorkflowElement)scells[0]).getPropertyObject() instanceof Lane) {
            en=true;
         }
      }
      setEnabled(en);
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gc = (GraphController)jawecomponent;
      if (gc.getSelectedGraph() != null) {
         Graph g=gc.getSelectedGraph();
         Lane cep=(Lane)((WorkflowElement)g.getSelectionCell()).getPropertyObject();
         ExpressionParticipantEditor ed=new ExpressionParticipantEditor(cep);
         ed.editXPDLElement();
      }
   }   
}
