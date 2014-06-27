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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphActivityInterface;
import org.enhydra.jawe.components.graph.GraphArtifactInterface;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphTransitionInterface;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Artifact;
import org.jgraph.graph.DefaultGraphCell;

/**
 * Sets the performer expression for all the activities contained inside selected
 * CommonExpressionParticipant.
 * 
 * @author Sasa Bojanic
 */
public class SetLabelLocation extends ActionBase {

   protected int location = -1;

   public SetLabelLocation(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public SetLabelLocation(JaWEComponent jawecomponent, int location) {
      super(jawecomponent);
      this.location = location;
   }

   public void enableDisableAction() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      GraphController gc = (GraphController) jawecomponent;
      Graph g = gc.getSelectedGraph();
      if (getPackage() == jc.getMainPackage() && g != null) {
         Object[] sc = g.getSelectionCells();
         boolean en = sc != null && sc.length > 0;
         for (Object object : sc) {
            if (!checkObject(object)) {
               en = false;
               break;
            }
         }
         setEnabled(en);
      } else {
         setEnabled(false);
      }
   }

   public void actionPerformed(ActionEvent e) {
      GraphController gc = (GraphController) jawecomponent;
      Graph graph = gc.getSelectedGraph();
      Object[] cells = graph.getSelectionCells();
      List toSelect = new ArrayList();
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      jc.startUndouableChange();
      gc.setUpdateInProgress(true);
      for (Object cell : cells) {
         if (checkObject(cell)) {
            XMLCollectionElement actOrArtif = (XMLCollectionElement) ((DefaultGraphCell) cell).getUserObject();
            GraphUtilities.setLabelLocation(actOrArtif, location);
            toSelect.add(actOrArtif);
         }
      }
      JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
      gc.setUpdateInProgress(false);
      graph.refresh();
   }

   private boolean checkObject(Object cell) {
      boolean ok = false;
      if (cell instanceof GraphArtifactInterface || cell instanceof GraphActivityInterface) {
         XMLCollectionElement actOrArtif = (XMLCollectionElement) ((DefaultGraphCell) cell).getUserObject();
         if (actOrArtif instanceof Artifact) {
            if (((Artifact) actOrArtif).getArtifactType().equals(XPDLConstants.ARTIFACT_TYPE_DATAOBJECT)) {
               ok = true;
            }
         } else {
            int actType = ((Activity) actOrArtif).getActivityType();
            if (actType == XPDLConstants.ACTIVITY_TYPE_EVENT_START
                || actType == XPDLConstants.ACTIVITY_TYPE_EVENT_END || actType == XPDLConstants.ACTIVITY_TYPE_ROUTE) {
               ok = true;
            }
         }
      }
      return ok;
   }
}
