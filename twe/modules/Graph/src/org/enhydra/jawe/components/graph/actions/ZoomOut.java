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

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Zoom out (for 15%)
 * 
 * @author Sasa Bojanic
 */
public class ZoomOut extends ActionBase {

   public ZoomOut(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController) jawecomponent;

      if (gc.getSelectedGraph() != null)
         if (gc.getSelectedGraph().getScale() > 0.1) {
            setEnabled(true);
            return;
         }

      setEnabled(false);
   }

   public void actionPerformed(ActionEvent e) {
      Graph selectedGraph = ((GraphController) jawecomponent).getSelectedGraph();
      if (selectedGraph == null)
         return;
      // setResizeAction(null);
      double scale = 0.85 * selectedGraph.getScale();
      scale = Math.max(Math.min(scale, 5), 0.1);
      selectedGraph.setScale(scale);
      selectedGraph.setPreferredSize(selectedGraph.getGraphManager().getGraphsPreferredSize());

      // With JGraph3.4.1 this causes problems
      if (selectedGraph.getSelectionCell() != null) {
         selectedGraph.scrollCellToVisible(selectedGraph.getSelectionCell());
      }

      GraphController gc = (GraphController) jawecomponent;
      gc.getSettings().getAction("ZoomIn").getAction().enableDisableAction();
      gc.getSettings().getAction("ZoomOut").getAction().enableDisableAction();
      gc.getSettings().getAction("ActualSize").getAction().enableDisableAction();
   }
}
