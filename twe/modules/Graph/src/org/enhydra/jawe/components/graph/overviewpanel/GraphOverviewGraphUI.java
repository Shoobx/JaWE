package org.enhydra.jawe.components.graph.overviewpanel;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.JaWEGraphUI;

/**
 * This class and it's inner classes controls mouse actions and clipboard.
 * It is addapted to get wanted editing cell behaviour, selection behaviour
 * , to implement cell overlaping, to implement right participant adjustment
 * after cell (or group of cells) is moved, and to implement proper copying
 * and pasting/cloning of cells, as well as pasting at wanted location (along
 * with right participant adjustment).
 */
public class GraphOverviewGraphUI extends JaWEGraphUI {

   /**
    * Returns graph.
    */
   public Graph getGraph() {
      return (Graph) graph;
   }

   /**
    * Creates the listener responsible for updating the selection based on
    * mouse events.
    */
   protected MouseListener createMouseListener() {
      return new PEMouseHandler();
   }

   /**
    * Handles selection in a way that we expect.
    */
   public class PEMouseHandler extends JaWEGraphUI.PEMouseHandler {

      public void mousePressed(MouseEvent e) {
         if (!graph.hasFocus()) graph.requestFocus();
         aborted = false;
         if (status == SELECTION && graph.isSelectionEnabled()) {
            // find where was clicked...
            int s = graph.getTolerance();

            Rectangle2D r = graph.fromScreen(new Rectangle(e.getX() - s, e.getY() - s, 2 * s, 2 * s));
            focus = (focus != null && focus.intersects(graph, r)) ? focus : null;
            Point2D point = graph.fromScreen(new Point(e.getPoint()));

            // changed from original because of overlapping
            if (focus == null) {
               cell = graph.getNextViewAt(focus, point.getX(), point.getY());
               focus = cell;
            } else {
               cell = focus;
            }

            // SIMPLE SELECTION
            if (cell != null) {
               if (e.getClickCount() == 2) {
                  startEditing(cell.getCell(), e);
               } else {
                  if (!graph.isCellSelected(cell.getCell())) {
                     selectCellForEvent(cell.getCell(), e);
                  }
                  if (handle!=null) {
                     handle.mousePressed(e);
                  }
               }
            } else {
               // MULTIPLE SELECTION
               marquee.mousePressed(e);
               status = MULTIPLE_SELECTION;
            }

         }

         e.consume();
      }
   }
}
