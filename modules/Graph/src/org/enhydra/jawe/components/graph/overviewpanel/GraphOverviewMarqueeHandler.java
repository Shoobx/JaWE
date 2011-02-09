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

package org.enhydra.jawe.components.graph.overviewpanel;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphMarqueeHandler;
import org.enhydra.jxpdl.elements.Participant;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;

/**
 * Implementation of a marquee handler for Process Editor. This is also a place
 * where (after mouse click or release) participants, activities (normal, subflows,
 * block activities) and transitions are inserted, where persistent mode is achived and
 * where mouse cursors are changing, and where popup menu is implemented. When
 * inserting cells it calls WorkflowManager.
 */
public class GraphOverviewMarqueeHandler extends GraphMarqueeHandler { //implements Serializable {

   public GraphOverviewMarqueeHandler (GraphController graphController) {
      super(graphController);
   }

   public void mouseReleased(MouseEvent ev) {
      try {
         if (ev != null && marqueeBounds != null) {
            Rectangle2D bounds = getGraphController().getOverview().getGraph().fromScreen(marqueeBounds);// HM, JGraph3.4.1
            CellView[] rootViews = getGraphController().getOverview().getGraph().getGraphLayoutCache().getRoots(bounds);

            // added - getting all views in model (except forbidden objects)
            CellView[] views = AbstractCellView.getDescendantViews(rootViews);
            ArrayList wholeList = new ArrayList();
            ArrayList participantList = new ArrayList();
            ArrayList otherList = new ArrayList();
            for (int i = 0; i < views.length; i++) {
               if (bounds.contains(views[i].getBounds())) {
                  if (views[i].getCell() instanceof DefaultGraphCell
                        && !(((DefaultGraphCell) views[i].getCell()).getUserObject() instanceof Participant)) {
                     otherList.add(views[i].getCell());
                  } else {
                     participantList.add(views[i].getCell());
                  }
                  wholeList.add(views[i].getCell());
               }
            }

            Object[] cells = wholeList.toArray();

            getGraphController().getOverview().getGraph().getUI().selectCellsForEvent(getGraph(), cells, ev);
            Rectangle dirty = marqueeBounds.getBounds();// HM, JGraph3.4.1
            dirty.width++;
            dirty.height++;// HM, JGraph3.4.1
            getGraphController().getOverview().getGraph().repaint(dirty);
         }
      } finally {
         currentPoint = null;
         startPoint = null;
         marqueeBounds = null;
      }
   }   
}
