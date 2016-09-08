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

package org.enhydra.jawe.components.graph;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEActions;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.jgraph.JGraph;
import org.jgraph.event.GraphLayoutCacheEvent;
import org.jgraph.event.GraphLayoutCacheListener;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.plaf.basic.BasicGraphUI;

/**
 * This class and it's inner classes controls mouse actions and clipboard. It is addapted to get wanted editing cell behaviour, selection behaviour , to
 * implement cell overlaping, to implement right participant adjustment after cell (or group of cells) is moved, and to implement proper copying and
 * pasting/cloning of cells, as well as pasting at wanted location (along with right participant adjustment).
 */
public class JaWEGraphUI extends BasicGraphUI {

   public final static int SELECTION = 0;

   public final static int MULTIPLE_SELECTION = 1;

   public final static int INSERT_ELEMENT = 2;

   public final static int INSERT_PARTICIPANT = 3;

   public final static int INSERT_SPEC_ELEMENT = 4;

   public final static int INSERT_TRANSITION_START = 5;

   public final static int INSERT_TRANSITION_POINTS = 6;

   public final static int INSERT_ASSOCIATION_START = 7;

   public final static int INSERT_ASSOCIATION_POINTS = 8;

   protected int status;

   protected boolean aborted = false;

   protected boolean selectOnRelease = false;

   /**
    * Returns graph.
    */
   public Graph getGraph() {
      return (Graph) graph;
   }

   public GraphController getGraphController() {
      return ((GraphMarqueeHandler) marquee).getGraphController();
   }

   /**
    * Paint the background of this graph. Calls paintGrid.
    */
   protected void paintBackground(Graphics g) {
      Rectangle pageBounds = new Rectangle(0, 0, graph.getWidth(), graph.getHeight());

      if (graph.isGridVisible()) {
         paintGrid(graph.getGridSize(), g, pageBounds);
      }
   }

   /**
    * This method is called by EditAction class, as well as by pressing F2 or clicking a mouse on a cell.
    */
   protected boolean startEditing(Object cell, MouseEvent event) {
      if (cell instanceof WorkflowElement) {
         XMLElement el = ((WorkflowElement) cell).getPropertyObject();
         if (el instanceof FreeTextExpressionParticipant || el instanceof CommonExpressionParticipant) {
            return true;
         }

         JaWEManager.getInstance().getJaWEController().getJaWEActions().getAction(JaWEActions.EDIT_PROPERTIES_ACTION).actionPerformed(null);
         return true;
      }
      return false;
   }

   // FIXED by xxp - there was a problem when zooming-out activity
   public void startEditingAtCell(JGraph pGraph, Object cell) {
      if (cell != null)
         startEditing(cell, null);
   }

   /**
    * Creates the listener responsible for updating the selection based on mouse events.
    */
   protected MouseListener createMouseListener() {
      return new PEMouseHandler();
   }

   /**
    * Handles selection in a way that we expect.
    */
   public class PEMouseHandler extends MouseHandler {

      public void mousePressed(MouseEvent e) {
         if (!graph.hasFocus())
            graph.requestFocus();
         aborted = false;
         selectOnRelease = false;
         if (status == SELECTION && graph.isSelectionEnabled()) {
            // find where was clicked...
            int s = graph.getTolerance();

            Rectangle2D r = graph.fromScreen(new Rectangle(e.getX() - s, e.getY() - s, 2 * s, 2 * s));
            // focus = (focus != null && focus.intersects(graph, r)) ? focus : null;
            focus = null;
            Point2D point = graph.fromScreen(new Point(e.getPoint()));

            // changed from original because of overlapping
            if (focus == null) {
               cell = graph.getNextViewAt(focus, point.getX(), point.getY());
               focus = cell;
            } else {
               cell = focus;
            }

            // if it's right mouse button show popup menu, otherwise user whish to select
            // something
            if (SwingUtilities.isRightMouseButton(e)) {
               // POPUP
               if (cell != null) {
                  if (!graph.isCellSelected(cell.getCell())) {
                     selectCellForEvent(cell.getCell(), e);
                  }
               } else {
                  graph.clearSelection();
               }

               ((GraphMarqueeHandler) marquee).popupMenu(e.getPoint());
            } else {
               // SIMPLE SELECTION
               if (cell != null) {
                  if (e.getClickCount() == 2) {
                     startEditing(cell.getCell(), e);
                  } else {
                     if (graph.isCellSelected(cell.getCell()) && !e.isControlDown() && graph.getSelectionCells().length > 1) {
                        selectOnRelease = true;
                     } else {
                        if (!graph.isCellSelected(cell.getCell()) || e.isControlDown()) {
                           selectCellForEvent(cell.getCell(), e);
                        }
                     }
                     if (cell.getCell() instanceof WorkflowElement) {
                        XMLElement el = ((WorkflowElement) cell.getCell()).getPropertyObject();
                        if (el instanceof Activity) {
                           Activity act = (Activity) el;
                           boolean descendAction = false;
                           if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_SUBFLOW || act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_BLOCK) {
                              Point p = e.getPoint();
                              Graph g = ((GraphMarqueeHandler) marquee).getGraph();

                              Rectangle rct = g.getGraphManager().getCBounds(cell.getCell(), null);
                              Rectangle r1 = new Rectangle((int) (rct.getMinX() + rct.getWidth() / 2 - 7), (int) (rct.getMaxY() - 16), 15, 15);
                              Rectangle r2 = new Rectangle(p);
                              if (SwingUtilities.isRectangleContainingRectangle(r1, r2)) {
                                 descendAction = true;
                              }
                           }
                           if (descendAction && act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_SUBFLOW) {
                              WorkflowProcess wp = XMLUtil.getSubflowProcess(JaWEManager.getInstance().getXPDLHandler(), act);
                              if (wp != null)
                                 getGraphController().selectGraphForElement(wp);
                           } else if (descendAction && act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_BLOCK) {
                              ActivitySet as = XMLUtil.getBlockActivitySet(act);
                              if (as != null)
                                 getGraphController().selectGraphForElement(as);
                           }
                        }
                     }
                     if (handle != null) {
                        handle.mousePressed(e);
                     }
                  }
               } else {
                  // MULTIPLE SELECTION
                  marquee.mousePressed(e);
                  status = MULTIPLE_SELECTION;
               }
            }
         }

         if (SwingUtilities.isRightMouseButton(e)) {
            if (status == INSERT_TRANSITION_POINTS) {
               status = INSERT_TRANSITION_START;
               ((GraphMarqueeHandler) marquee).reset();
            } else if (status == INSERT_ASSOCIATION_POINTS) {
               status = INSERT_ASSOCIATION_START;
               ((GraphMarqueeHandler) marquee).reset();
            } else {
               ((GraphMarqueeHandler) marquee).setSelectionMode();
               status = SELECTION;
            }
         } else {
            if (graph.isEditable()) {
               if (status == INSERT_PARTICIPANT) {
                  // maybe latter we can add inserting participants where user choose, not
                  // at end...
                  ((GraphMarqueeHandler) marquee).insertParticipant((Point) getGraph().fromScreen(e.getPoint()));
               } else if (status == INSERT_SPEC_ELEMENT) {
                  // this is reserved for special buttons... like block or process. Maybe
                  // we should move them somewhere.
                  ((GraphMarqueeHandler) marquee).insertSpecialElement();
               } else if (status == INSERT_ELEMENT) {
                  ((GraphMarqueeHandler) marquee).insertElement((Point) getGraph().fromScreen(e.getPoint()));
               } else if (status == INSERT_TRANSITION_START) {
                  GraphPortViewInterface gpvi = (GraphPortViewInterface) graph.getPortViewAt(e.getX(), e.getY());
                  if (gpvi != null) {
                     if (((GraphMarqueeHandler) marquee).insertTransitionFirstPort(gpvi)) {
                        status = INSERT_TRANSITION_POINTS;
                     }
                  }
               } else if (status == INSERT_ASSOCIATION_START) {
                  GraphPortViewInterface gpvi = (GraphPortViewInterface) graph.getPortViewAt(e.getX(), e.getY());
                  if (gpvi != null) {
                     if (((GraphMarqueeHandler) marquee).insertTransitionFirstPort(gpvi)) {
                        status = INSERT_ASSOCIATION_POINTS;
                     }
                  }
               } else if (status == INSERT_TRANSITION_POINTS) {
                  GraphPortViewInterface gpvi = (GraphPortViewInterface) graph.getPortViewAt(e.getX(), e.getY());
                  if (gpvi == null) {
                     ((GraphMarqueeHandler) marquee).addPoint(e.getPoint());
                     ((GraphMarqueeHandler) marquee).drawTransition(e);
                  } else {
                     if (((GraphMarqueeHandler) marquee).insertTransitionSecondPort(gpvi)) {
                        status = INSERT_TRANSITION_START;
                        ((GraphMarqueeHandler) marquee).reset();
                     }
                  }
               } else if (status == INSERT_ASSOCIATION_POINTS) {
                  GraphPortViewInterface gpvi = (GraphPortViewInterface) graph.getPortViewAt(e.getX(), e.getY());
                  if (gpvi == null) {
                     ((GraphMarqueeHandler) marquee).addPoint(e.getPoint());
                     ((GraphMarqueeHandler) marquee).drawTransition(e);
                  } else {
                     if (((GraphMarqueeHandler) marquee).insertTransitionSecondPort(gpvi)) {
                        status = INSERT_ASSOCIATION_START;
                        ((GraphMarqueeHandler) marquee).reset();
                     }
                  }
               }
            } else {
               // maybe display info... external package so can't be edited...
            }
         }

         e.consume();
      }

      public void mouseMoved(MouseEvent e) {
         if (status == INSERT_TRANSITION_START
             || status == INSERT_TRANSITION_POINTS || status == INSERT_ASSOCIATION_START || status == INSERT_ASSOCIATION_POINTS) {
            ((GraphMarqueeHandler) marquee).drawTransition(e);
         }

         e.consume();
      }

      public void mouseDragged(MouseEvent e) {
         if (status == SELECTION && !aborted) {
            // added - if one of selected cell is Participant there must be no dragging
            Object[] sc = graph.getSelectionCells();
            if (sc != null) {
               for (int i = 0; i < sc.length; i++) {
                  if (sc[i] instanceof GraphSwimlaneInterface) {
                     e.consume();
                     return;
                  }
               }
            }

            selectOnRelease = false;
            autoscroll(graph, e.getPoint());
            if (handle != null) {
               handle.mouseDragged(e);
            }
         } else if (status == MULTIPLE_SELECTION && !aborted) {
            // drag rectangle for multiply selection
            marquee.mouseDragged(e);
         }

         e.consume();
      }

      public void mouseReleased(MouseEvent e) {
         if (status == SELECTION) {
            if (handle != null && !aborted) {
               handle.mouseReleased(e);
            }

            if (selectOnRelease) {
               // graph.clearSelection();
               selectCellForEvent(cell.getCell(), e);
            }
         } else if (status == MULTIPLE_SELECTION && graph.isSelectionEnabled() && !aborted) {
            marquee.mouseReleased(e);
            status = SELECTION;
         }

         e.consume();
      }
   }

   /**
    * Constructs the "root handle" for <code>context</code>.
    * 
    * @param context reference to the context of the current selection.
    */
   public CellHandle createHandle(GraphContext context) {
      if (context != null && !context.isEmpty() && graph.isEnabled())
         return new PERootHandle(context);
      return null;
   }

   /**
    * Manages selection movement. It is adapted to suport proper undo in coordination with WorkflowManager class.
    */
   public class PERootHandle extends RootHandle {
      /**
       * Creates a root handle which contains handles for the given cells. The root handle and all its childs point to the specified JGraph instance. The root
       * handle is responsible for dragging the selection.
       */
      public PERootHandle(GraphContext ctx) {
         super(ctx);
      }

      protected Point2D getInitialLocation(Object[] cells) {
         try {
            return super.getInitialLocation(cells);
         } catch (Throwable thr) {
            return null;
         }
      }

      public void mouseReleased(MouseEvent event) {
         if (event != null && !event.isConsumed()) {
            if (activeHandle != null) {
               activeHandle.mouseReleased(event);
               activeHandle = null;
            } else if (isMoving && !event.getPoint().equals(start)) {
               if (cachedBounds != null) {
                  double dx = event.getX() - start.getX();// HM, JGraph3.4.1
                  double dy = event.getY() - start.getY();// HM, JGraph3.4.1
                  Point2D tmp = graph.fromScreen(new Point2D.Double(dx, dy));// HM,
                                                                             // JGraph3.4.1
                  GraphLayoutCache.translateViews(views, tmp.getX(), tmp.getY());// HM,
                                                                                 // JGraph3.4.1
               }

               // Harald Meister: snap activities to grid if grid is enabled
               if (GraphUtilities.getGraphController().getGraphSettings().shouldShowGrid()
                   && (views[0] instanceof GraphActivityViewInterface || views[0] instanceof GraphActivityRendererInterface)) {

                  CellView view = views[0];
                  Rectangle2D rect = view.getBounds();// HM, JGraph3.4.1
                  double dx = 0;
                  double dy = 0;

                  double gridsize = GraphUtilities.getGraphController().getGraphSettings().getGridSize();
                  double deltax = (int) rect.getX() % gridsize;
                  double deltay = (int) rect.getY() % gridsize;
                  double halfgrid = gridsize / 2;
                  if (deltax > halfgrid) {
                     dx += (gridsize - deltax);
                  } else {
                     dx -= deltax;
                  }
                  if (deltay > halfgrid) {
                     dy += (gridsize - deltay);
                  } else {
                     dy -= deltay;
                  }
                  Point2D tmp = graph.fromScreen(new Point2D.Double(dx, dy));// HM,
                                                                             // JGraph3.4.1
                  GraphLayoutCache.translateViews(views, tmp.getX(), tmp.getY());// HM,
                                                                                 // JGraph3.4.1
               }
               // Harald Meister

               CellView[] all = graphLayoutCache.getAllDescendants(views);

               if (graph.isMoveable()) { // Move Cells
                  // if (!graphModel.isAttributeStore()) {
                  // Map propertyMap = GraphConstants.createPropertyMap(all,null);
                  Map propertyMap = GraphConstants.createAttributes(all, null);
                  GraphManager gm = getGraph().getGraphManager();
                  gm.moveCellsAndArrangeParticipants(propertyMap);
                  /*
                   * } else { Map propertyMap =
                   * GraphConstants.createPropertyMap(all,null); WorkflowManager
                   * dm=getGraph().getWorkflowManager();
                   * dm.moveCellsAndArrangeParticipants(propertyMap); }
                   */
               }
               event.consume();
            }
         }
         start = null;
      }

   }

   /**
    * Returns a listener that can update the graph when the view changes.
    */
   protected GraphLayoutCacheListener createGraphLayoutCacheListener() {
      return new PEGraphLayoutCacheHandler();
   }

   /**
    * This class observes view changes and is adapted to disallow deselection of cells after dragging.
    */
   public class PEGraphLayoutCacheHandler extends GraphLayoutCacheHandler {
      /*
       * (non-Javadoc)
       * @see
       * org.jgraph.event.GraphLayoutCacheListener#graphLayoutCacheChanged(org.jgraph.
       * event.GraphLayoutCacheEvent)
       */
      public void graphLayoutCacheChanged(GraphLayoutCacheEvent e) {
         Object[] changed = e.getChange().getChanged();
         if (changed != null && changed.length > 0) {
            for (int i = 0; i < changed.length; i++) {
               graph.updateAutoSize(graphLayoutCache.getMapping(changed[i], false));
            }
         }
         Object[] inserted = e.getChange().getInserted();
         if (inserted != null
             && inserted.length > 0 && graphLayoutCache.isSelectsLocalInsertedCells()
             && !(graphLayoutCache.isSelectsAllInsertedCells() && !graphLayoutCache.isPartial()) && graph.isEnabled()) {
            Object[] roots = DefaultGraphModel.getRoots(graphModel, inserted);
            if (roots != null && roots.length > 0) {
               focus = graphLayoutCache.getMapping(roots[0], false);
               graph.setSelectionCells(roots);
            }
         }
         updateSize();
      }
   }

   /**
    * Returns a listener that can update the graph when the model changes.
    */
   // protected GraphModelListener createGraphModelListener() {
   // return new PEGraphModelHandler();
   // }

   /**
    * Listens for changes in the graph model and updates the view accordingly.
    */
   public class PEGraphModelHandler implements GraphModelListener, Serializable {

      public void graphChanged(GraphModelEvent e) {
         Object[] removed = e.getChange().getRemoved();
         // Remove from selection & focus
         if (removed != null && removed.length > 0) {
            // Update Focus If Necessary
            for (int i = 0; i < removed.length && focus != null; i++) {
               if (removed[i] == focus.getCell()) {
                  focus = null;
                  break;
               }
            }
            // Remove from selection
            graph.getSelectionModel().removeSelectionCells(removed);
         }
         if (graphLayoutCache != null)
            graphLayoutCache.graphChanged(e.getChange());
         // Get arrays
         Object[] inserted = e.getChange().getInserted();
         Object[] changed = e.getChange().getChanged();
         // Insert
         if (inserted != null && inserted.length > 0) {
            // Update focus to first inserted cell
            focus = graphLayoutCache.getMapping(inserted[0], false);
            for (int i = 0; i < inserted.length; i++)
               graph.updateAutoSize(graphLayoutCache.getMapping(inserted[i], false));
         }
         // Change (update size)
         if (changed != null && changed.length > 0) {
            for (int i = 0; i < changed.length; i++)
               graph.updateAutoSize(graphLayoutCache.getMapping(changed[i], false));
         }
         // Select if not partial
         if (!graphLayoutCache.isPartial() && graphLayoutCache.isSelectsAllInsertedCells() && graph.isEnabled()) {
            // Object[] roots = JaWEGraphModel.getRoots(graphModel, inserted);
            // if (roots != null && roots.length > 0) {
            // focus = graphLayoutCache.getMapping(roots[0], false);
            // graph.setSelectionCells(roots);
            // }
            graph.setSelectionCells(inserted);
         }
         updateSize();
      }

   } // End of BasicGraphUI.GraphModelHandler

   public void reset() {
      status = ((GraphMarqueeHandler) marquee).getStatus();
   }

   /**
    * Creates the listener reponsible for getting key events from the graph.
    */
   protected KeyListener createKeyListener() {
      return new PEKeyHandler();
   }

   /**
    * This is used to get multiple key down events to appropriately generate events.
    */
   public class PEKeyHandler extends KeyAdapter implements Serializable {
      /** Key code that is being generated for. */
      protected Action repeatKeyAction;

      /** Set to true while keyPressed is active. */
      protected boolean isKeyDown;

      public void keyPressed(KeyEvent e) {
         KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());
         if (keyStroke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (marquee != null)
               marquee.mouseReleased(null);
            ((GraphMarqueeHandler) marquee).setSelectionMode();
            aborted = true;
         } else if (e.isControlDown() && keyStroke.getKeyCode() == KeyEvent.VK_A) {
            e.consume();
            Graph g = ((GraphMarqueeHandler) marquee).getGraph();
            List cells = JaWEGraphModel.getAllCellsInModel(g.getModel());
            Iterator it = cells.iterator();
            while (it.hasNext()) {
               Object cell = it.next();
               if (cell instanceof GraphSwimlaneInterface || cell instanceof GraphPortInterface) {
                  it.remove();
               }
            }
            g.clearSelection();
            g.selectElements(cells.toArray(), false, false);
         }

      }

      public void keyReleased(KeyEvent e) {
      }
   }
}
