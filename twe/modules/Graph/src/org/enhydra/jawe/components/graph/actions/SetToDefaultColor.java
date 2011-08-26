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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.FreeTextExpressionParticipant;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphTransitionInterface;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.jawe.components.graph.WorkflowElement;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.elements.ConnectorGraphicsInfo;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.NodeGraphicsInfo;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Pool;

/**
 * Set selected graph object's colors to default values.
 * 
 * @author Sasa Bojanic
 */
public class SetToDefaultColor extends ActionBase {

   public SetToDefaultColor(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController) jawecomponent;
      setEnabled(gc.getSelectedGraph() != null
                 && !gc.getSelectedGraph().getXPDLObject().isReadOnly());
   }

   public void actionPerformed(ActionEvent e) {
      GraphController gcon = (GraphController) jawecomponent;

      Object[] cells = gcon.getSelectedGraph().getSelectionCells();
      gcon.setUpdateInProgress(true);
      JaWEManager.getInstance().getJaWEController().startUndouableChange();

      for (int i = 0; i < cells.length; i++) {
         Object cell = cells[i];
         if (cell instanceof WorkflowElement) {
            XMLComplexElement el = ((WorkflowElement) cell).getPropertyObject();
            Color c = null;
            if (el instanceof Pool || el instanceof Lane) {
               Object par = null;
               if (el instanceof Lane) {
                  Lane l = (Lane) el;
                  par = GraphUtilities.getParticipantForLane(l, null);
                  if (par == null && l.getPerformers().size() > 0) {
                     par = l;
                  }
               }
               if (par == null) {
                  par = FreeTextExpressionParticipant.getInstance();
               }

               if (par instanceof FreeTextExpressionParticipant) {
                  c = GraphUtilities.getGraphController()
                     .getGraphSettings()
                     .getLaneFreeTextExpressionColor();
               } else if (par instanceof Lane) {
                  c = GraphUtilities.getGraphController()
                     .getGraphSettings()
                     .getLaneCommonExpressionColor();
               } else {
                  c = JaWEManager.getInstance()
                     .getJaWEController()
                     .getTypeResolver()
                     .getJaWEType((Participant) par)
                     .getColor();
               }
            } else {
               c = JaWEManager.getInstance()
                  .getJaWEController()
                  .getTypeResolver()
                  .getJaWEType(el)
                  .getColor();
            }
            if (c != null) {
               if (!(cell instanceof GraphTransitionInterface)) {
                  NodeGraphicsInfo ngi = JaWEManager.getInstance()
                     .getXPDLUtils()
                     .getNodeGraphicsInfo((XMLCollectionElement) el);
                  if (ngi != null) {
                     ngi.setFillColor(String.valueOf(c.getRed())
                                      + "," + String.valueOf(c.getGreen()) + ","
                                      + String.valueOf(c.getBlue()));
                  }
               } else {
                  ConnectorGraphicsInfo cgi = JaWEManager.getInstance()
                     .getXPDLUtils()
                     .getConnectorGraphicsInfo((XMLCollectionElement) el);
                  if (cgi != null) {
                     cgi.setFillColor(String.valueOf(c.getRed())
                                      + "," + String.valueOf(c.getGreen()) + ","
                                      + String.valueOf(c.getBlue()));
                  }
               }
            }
         }
      }
      List toSelect = new ArrayList(JaWEManager.getInstance()
                                    .getJaWEController()
                                    .getSelectionManager()
                                    .getSelectedElements());
      JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
      gcon.setUpdateInProgress(false);

      gcon.getSelectedGraph().refresh();
   }
}
