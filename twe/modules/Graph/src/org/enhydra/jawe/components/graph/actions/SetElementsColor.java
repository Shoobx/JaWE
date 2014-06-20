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

package org.enhydra.jawe.components.graph.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphCommonInterface;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphSwimlaneInterface;
import org.enhydra.jawe.components.graph.GraphTransitionInterface;
import org.enhydra.jawe.components.graph.WorkflowElement;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.elements.ConnectorGraphicsInfo;
import org.enhydra.jxpdl.elements.NodeGraphicsInfo;

/**
 * Sets the performer expression for all the activities contained inside selected
 * CommonExpressionParticipant.
 * 
 * @author Sasa Bojanic
 */
public class SetElementsColor extends ActionBase {

   public SetElementsColor(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController) jawecomponent;

      boolean en = false;
      if (gc.getSelectedGraph() != null) {
         Graph g = gc.getSelectedGraph();
         Object[] scells = g.getSelectionCells();
         if (!g.getXPDLObject().isReadOnly() && scells != null && scells.length > 0) {
            en = true;
         }
      }
      setEnabled(en);
   }

   public void actionPerformed(ActionEvent e) {
      GraphController gc = (GraphController) jawecomponent;
      if (gc.getSelectedGraph() != null) {
         Graph g = gc.getSelectedGraph();

         Object[] cell = g.getSelectionCells();
         List els2edit = new ArrayList();
         Color defC = Color.BLACK;
         for (int i = 0; i < cell.length; i++) {
            XMLElement fc = null;
            if (cell[i] instanceof GraphCommonInterface
                || cell[i] instanceof GraphSwimlaneInterface) {
               NodeGraphicsInfo ngi = JaWEManager.getInstance()
                  .getXPDLUtils()
                  .getNodeGraphicsInfo((XMLCollectionElement) ((WorkflowElement) cell[i]).getPropertyObject());
               fc = ngi.get("FillColor");
            } else if (cell[i] instanceof GraphTransitionInterface) {
               ConnectorGraphicsInfo cgi = JaWEManager.getInstance()
                  .getXPDLUtils()
                  .getConnectorGraphicsInfo((XMLCollectionElement) ((WorkflowElement) cell[i]).getPropertyObject(), false);
               fc = cgi.get("FillColor");
            }
            els2edit.add(fc);
            if (i == 0) {
               defC = Utils.getColor(fc.toValue());
            }
         }
         if (els2edit.size() > 0) {
            Color c = JColorChooser.showDialog(JaWEManager.getInstance()
                                                  .getJaWEController()
                                                  .getJaWEFrame(),
                                               ResourceManager.getLanguageDependentString("SetElementsColorLabel"),
                                               defC);
            if (c != null) {
               JaWEManager.getInstance().getJaWEController().startUndouableChange();
               String cs = Utils.getColorString(c);
               for (int i = 0; i < els2edit.size(); i++) {
                  ((XMLElement) els2edit.get(i)).setValue(cs);
               }
               
               List toSelect = new ArrayList(JaWEManager.getInstance()
                                             .getJaWEController()
                                             .getSelectionManager()
                                             .getSelectedElements());
               JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
            }
         }
      }
   }
}
