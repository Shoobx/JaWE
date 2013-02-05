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

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Set the graph scale to 100%.
 * @author Sasa Bojanic
 */
public class ActualSize extends ActionBase {

   public ActualSize (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getSelectedGraph() != null && gc.getSelectedGraph().getScale() != 1)
         setEnabled(true);
      else
         setEnabled(false);
   }
   
   public void actionPerformed(ActionEvent e) {
      Graph selectedGraph=((GraphController)jawecomponent).getSelectedGraph();
      if (selectedGraph==null) return;
      //setResizeAction(null);
      double curScale=selectedGraph.getScale();
      try {
         Dimension prefSize=selectedGraph.getSize();
         prefSize.width=(int)(prefSize.width/curScale);
         prefSize.height=(int)(prefSize.height/curScale);
         selectedGraph.setPreferredSize(selectedGraph.getGraphManager().getGraphsPreferredSize());
      } catch (Exception ex) {}

      selectedGraph.setScale(1);
      if (selectedGraph.getSelectionCell() != null) {
         selectedGraph.scrollCellToVisible(selectedGraph.getSelectionCell());
      }
      
      GraphController gc = (GraphController)jawecomponent;
      gc.getSettings().getAction("ZoomIn").getAction().enableDisableAction();      
      gc.getSettings().getAction("ZoomOut").getAction().enableDisableAction();
      gc.getSettings().getAction("ActualSize").getAction().enableDisableAction();
   }
   
}
