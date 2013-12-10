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
import org.enhydra.jawe.components.graph.GraphActivityInterface;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphTransitionInterface;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Transition;
import org.jgraph.graph.DefaultGraphCell;

/**
 * @author Sasa Bojanic
 *
 */
public class SelectConnectingActivitiesForSelectedTransitions extends ActionBase {

   public SelectConnectingActivitiesForSelectedTransitions (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }
   
   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getSelectedGraph() != null) {         
         Object[] selCels=gc.getSelectedGraph().getSelectionCells();
         boolean en=false;
         if (selCels!=null && selCels.length>0) {
            for (int i = 0; i < selCels.length; i++) {
               if (selCels[i] instanceof DefaultGraphCell) {
                  DefaultGraphCell dgc=(DefaultGraphCell)selCels[i];
                  if (dgc.getUserObject() instanceof Transition) {
                     en=true;
                     break;
                  }
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
      if (selectedGraph == null) return;
                  
      Object[] selCels=selectedGraph.getSelectionCells();
      if (selCels!=null && selCels.length>0) {
         Set actsToSelect=new HashSet();
         for (int i = 0; i < selCels.length; i++) {
            if (selCels[i] instanceof DefaultGraphCell) {
               DefaultGraphCell dgc=(DefaultGraphCell)selCels[i];
               if (dgc.getUserObject() instanceof Transition) {
                  GraphTransitionInterface tra = (GraphTransitionInterface)dgc;
                  actsToSelect.add(tra.getTargetActivityOrArtifact());
                  actsToSelect.add(tra.getSourceActivityOrArtifact());                  
               }
            }
         }      
         Iterator it=actsToSelect.iterator();
         while (it.hasNext()) {
            GraphActivityInterface gai=(GraphActivityInterface)it.next();
            selectedGraph.addSelectionCell(gai);
         }
      }
      
   }
   
}
