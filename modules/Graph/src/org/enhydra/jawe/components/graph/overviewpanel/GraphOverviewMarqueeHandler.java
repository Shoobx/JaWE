package org.enhydra.jawe.components.graph.overviewpanel;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphMarqueeHandler;
import org.enhydra.shark.xpdl.elements.Participant;
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
