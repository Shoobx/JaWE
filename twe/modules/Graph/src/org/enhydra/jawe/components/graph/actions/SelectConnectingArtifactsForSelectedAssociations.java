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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphCommonInterface;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphTransitionInterface;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Association;

/**
 * @author Sasa Bojanic
 */
public class SelectConnectingArtifactsForSelectedAssociations extends ActionBase {

   public SelectConnectingArtifactsForSelectedAssociations(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController) jawecomponent;

      if (gc.getSelectedGraph() != null) {
         Object[] selCels = gc.getSelectedGraph().getSelectionCells();
         boolean en = false;
         if (selCels != null && selCels.length > 0) {
            en = true;
            for (int i = 0; i < selCels.length; i++) {
               if (!(selCels[i] instanceof GraphTransitionInterface && ((GraphTransitionInterface) selCels[i]).getPropertyObject() instanceof Association)) {
                  en = false;
                  break;
               }
            }
         }
         setEnabled(en);
      } else {
         setEnabled(false);
      }
   }

   public void actionPerformed(ActionEvent e) {
      GraphController gcon = (GraphController) jawecomponent;
      Graph selectedGraph = gcon.getSelectedGraph();
      if (selectedGraph == null)
         return;

      Object[] selCels = selectedGraph.getSelectionCells();
      if (selCels != null && selCels.length > 0) {
         Set actsToSelect = new HashSet();
         for (int i = 0; i < selCels.length; i++) {
            if (selCels[i] instanceof GraphTransitionInterface) {
               GraphTransitionInterface tra = (GraphTransitionInterface) selCels[i];
               actsToSelect.add(tra.getTargetActivityOrArtifact());
               actsToSelect.add(tra.getSourceActivityOrArtifact());
            }
         }
         Iterator it = actsToSelect.iterator();
         while (it.hasNext()) {
            GraphCommonInterface gai = (GraphCommonInterface) it.next();
            if (gai.getPropertyObject() instanceof Artifact) {
               selectedGraph.addSelectionCell(gai);
            }
         }
      }

   }

}
