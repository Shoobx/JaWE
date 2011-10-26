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
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.ConnectorGraphicsInfo;
import org.enhydra.jxpdl.elements.Transition;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;

/**
 * Represents a view for a model's Transition object.
 * 
 * @author Sasa Bojanic
 */
public class DefaultGraphTransitionView extends GraphTransitionViewInterface {

   /**
    * Map of renderers for different transition types (keys: type of transitions, values:
    * renderer instances).
    */
   protected static Map renderers = new HashMap();

   /**
    * Constructs a transition view for the specified model object.
    * 
    * @param cell reference to the model object
    */
   public DefaultGraphTransitionView(Object cell) {
      super(cell);
      int x = (GraphConstants.PERMILLE / 2);
      int y = (GraphConstants.PERMILLE / 100);
      Point center = new Point(x, y);
      AttributeMap map = new AttributeMap();
      GraphConstants.setLabelPosition(map, center);
      GraphConstants.setAutoSize(map, true);
      this.setAttributes(map);
   }

   public CellViewRenderer getRenderer() {
      String type = ((GraphTransitionInterface) super.getCell()).getType();
      GraphTransitionRendererInterface gtrenderer = (GraphTransitionRendererInterface) renderers.get(type);
      if (gtrenderer == null) {
         gtrenderer = createRenderer((XMLCollectionElement) ((GraphTransitionInterface) super.getCell()).getUserObject());
         renderers.put(type, gtrenderer);
      }
      return gtrenderer;
   }

   public void mergeAttributes() {
      super.mergeAttributes();
   }

   public CellHandle getHandle(GraphContext context) {
      return new TransitionHandle(this, context);
   }

   public void addPoint(Graph graph, Point popupPoint) {
      boolean bendable = graph.isBendable() && GraphConstants.isBendable(getAttributes());
      if (bendable) {
         int index = -1;
         int s = graph.getHandleSize();
         // Rectangle rect = (Rectangle)graph.fromScreen(new
         // Rectangle(popupPoint.x-s,popupPoint.y-s,2*s,2*s));
         Rectangle rect = new Rectangle(popupPoint.x - s, popupPoint.y - s, 2 * s, 2 * s);
         // System.err.println("Rect=" + rect + ", popup=" + popupPoint);
         if (intersects(graph, rect)) {
            Point point = new Point(popupPoint); // (Point) graph.snap(new
            // Point(popupPoint));//HM, JGraph3.4.1
            double min = Double.MAX_VALUE, dist = 0;
            for (int i = 0; i < getPointCount() - 1; i++) {
               Point p = new Point((int) getPoint(i).getX(), (int) getPoint(i).getY());// HM,
               // JGraph3.4.1
               Point p1 = new Point((int) getPoint(i + 1).getX(),
                                    (int) getPoint(i + 1).getY());// HM, JGraph3.4.1

               // Point p = (Point) graph.snap(new Point((int) getPoint(i).getX(), (int)
               // getPoint(i).getY()));//HM, JGraph3.4.1
               // Point p1 = new Point((int) getPoint(i + 1).getX(), (int) getPoint(i +
               // 1).getY());//HM, JGraph3.4.1
               dist = new Line2D.Double(p, p1).ptLineDistSq(point);
               // System.err.println("P="
               // + p + ", P1=" + p1 + ", popup=" + point + ", dist="
               // + dist + ", min=" + min + ", index=" + index);
               if (dist < min) {
                  min = dist;
                  index = i + 1;
               }
            }
            if (index != -1) {
               addPoint(index, point);
               Map propertyMap = new HashMap();
               AttributeMap edgeMap = new AttributeMap(((GraphCell) cell).getAttributes());
               GraphConstants.setPoints(edgeMap, points);
               propertyMap.put(cell, edgeMap);
               ((JaWEGraphModel) graph.getModel()).insertAndEdit(null,
                                                                 propertyMap,
                                                                 null,
                                                                 null,
                                                                 null);
            }
         }
      }
   }

   public void removePoint(Graph graph, Point popupPoint) {
      boolean bendable = graph.isBendable() && GraphConstants.isBendable(getAttributes());
      if (bendable) {
         int index = -1;
         int s = graph.getHandleSize();
         // Rectangle rect = graph.fromScreen(new
         // Rectangle(popupPoint.x-s,popupPoint.y-s,2*s,2*s));
         Rectangle rect = new Rectangle(popupPoint.x - s, popupPoint.y - s, 2 * s, 2 * s);
         // System.err.println("Rect=" + rect + ", popup=" + popupPoint);
         if (intersects(graph, rect)) {
            Point point = new Point(popupPoint);// (Point) graph.snap(new
            // Point(popupPoint));//HM, JGraph3.4.1
            double min = Double.MAX_VALUE, dist = 0;
            for (int i = 0; i < getPointCount(); i++) {
               Point p = new Point((int) getPoint(i).getX(), (int) getPoint(i).getY());// HM,
               // JGraph3.4.1
               dist = Math.sqrt(((Point2D) point).distanceSq(p));
               // System.err.println("P="
               // + p + ", popup=" + point + ", dist=" + dist + ", min="
               // + min + ", index=" + index);

               if (dist < min) {
                  min = dist;
                  index = i;
               }
            }
            if (index != -1 && min <= s + 2 && index != 0 && index != getPointCount() - 1) {
               removePoint(index);
               Map propertyMap = new HashMap();
               AttributeMap edgeMap = new AttributeMap(((GraphCell) cell).getAttributes());
               GraphConstants.setPoints(edgeMap, points);
               propertyMap.put(cell, edgeMap);
               ((JaWEGraphModel) graph.getModel()).insertAndEdit(null,
                                                                 propertyMap,
                                                                 null,
                                                                 null,
                                                                 null);
            }
         }
      }
   }

   /**
    * Extends JGraph's EdgeHandle to implement interaction with XPDL model and special
    * check on possible connecting options.
    */
   public static class TransitionHandle extends EdgeHandle {

      /**
       * Creates new object.
       * 
       * @param edge The view.
       * @param ctx The context.
       */
      public TransitionHandle(EdgeView edge, GraphContext ctx) {
         super(edge, ctx);
      }

      public void mouseDragged(MouseEvent event) {
         super.mouseDragged(event);
      }

      public void mouseReleased(MouseEvent e) {
         try {
            if (source || target) {
               GraphTransitionInterface tr = (GraphTransitionInterface) edge.getCell();
               // GraphPortViewInterface ss = (GraphPortViewInterface)
               // graph.getGraphLayoutCache().getMapping(
               // tr.getSource(), false);
               // Object tt = (PortView)
               // graph.getGraphLayoutCache().getMapping(tr.getTarget(), false);
               GraphPortViewInterface pvs = (GraphPortViewInterface) edge.getSource();
               GraphPortViewInterface pvt = (GraphPortViewInterface) edge.getTarget();
               GraphCommonInterface s1 = tr.getSourceActivityOrArtifact();
               GraphCommonInterface t1 = tr.getTargetActivityOrArtifact();

               GraphCommonInterface s2 = null;
               try {
                  s2 = (GraphCommonInterface) ((GraphPortInterface) pvs.getCell()).getParent();
               } catch (Exception ex) {
               }
               GraphCommonInterface t2 = null;
               try {
                  t2 = (GraphCommonInterface) ((GraphPortInterface) pvt.getCell()).getParent();
               } catch (Exception ex) {
               }

               // System.out.println("source="+source+", target="+target);
               // System.out.println("S1="+s1+", T1="+t1);
               // System.out.println("S2="+s2+", T2="+t2);
               // System.out.println("ss="+ss.hashCode()+", tt="+tt.hashCode());
               // System.out.println("ss="+ss.getClass().getName()+", tt="+tt.getClass().getName());
               // System.out.println("pvs="+pvs.hashCode()+", pvt="+pvt.hashCode());
               // System.out.println("pvs="+pvs.getGraphActivity()+", pvt="+pvt.getGraphActivity());

               if (s1 != s2 || t1 != t2) {
                  GraphMarqueeHandler jmh = (GraphMarqueeHandler) graph.getMarqueeHandler();
                  XMLCollectionElement uo = (XMLCollectionElement) tr.getPropertyObject();
                  boolean accept = jmh.validateConnection(pvs, pvt, uo);
                  if (!accept) {
                     clean(tr);
                     return;
                  }
                  JaWEController jc = JaWEManager.getInstance().getJaWEController();
                  ((Graph) graph).getGraphController().setUpdateInProgress(true);
                  jc.startUndouableChange();
                  setChanges();

                  // must set source and target activity objects after inserting into
                  // model
                  String from = ((XMLCollectionElement) s2.getPropertyObject()).getId();
                  String to = ((XMLCollectionElement) t2.getPropertyObject()).getId();
                  if (uo instanceof Transition) {
                     ((Transition) uo).setFrom(from);
                     ((Transition) uo).setTo(to);
                  } else {
                     ((Association) uo).setSource(from);
                     ((Association) uo).setTarget(to);
                  }
                  if (from.equals(to)) {
                     ConnectorGraphicsInfo bpea = JaWEManager.getInstance()
                        .getXPDLUtils()
                        .getConnectorGraphicsInfo(uo);
                     if (bpea == null) {
                        GraphManager gmgr = ((Graph) graph).getGraphManager();
                        GraphActivityInterface gact = gmgr.getGraphActivity(from);
                        Point realP = new Point(50, 50);
                        if (gact != null) {
                           realP = gmgr.getCenter(gact);
                        }
                        List breakpoints = new ArrayList();
                        int rp50x1 = realP.x - 50;
                        int rp50x2 = realP.x + 50;
                        if (rp50x1 < 0) {
                           rp50x2 = rp50x2 - rp50x1;
                           rp50x1 = 0;
                        }
                        int rp50y = realP.y - 50;
                        if (rp50y < 0)
                           rp50y = realP.y + 50;

                        Point p1 = new Point(Math.abs(rp50x1), Math.abs(rp50y));
                        Point p2 = new Point(Math.abs(rp50x2), Math.abs(rp50y));
                        breakpoints.add(p1);
                        breakpoints.add(p2);

                        GraphUtilities.createConnectorGraphicsInfo(uo, breakpoints, true);
                        Map propertyMap = new HashMap();
                        AttributeMap map = new AttributeMap(edge.getAttributes());
                        propertyMap.put(edge.getCell(), map);
                        Point2D ps = edge.getPoint(0);
                        Point2D pt = edge.getPoint(1);
                        List points = new ArrayList();
                        points.add(ps);
                        points.addAll(breakpoints);
                        points.add(pt);
                        // JaWEManager.getInstance().getLoggingManager().debug("Updating breakpoints for transition: "+points);
                        GraphConstants.setPoints(map, points);
                        ((JaWEGraphModel) graph.getModel()).insertAndEdit(null,
                                                                          propertyMap,
                                                                          null,
                                                                          null,
                                                                          null);
                     }

                  }

                  List toSelect = new ArrayList();
                  toSelect.add(uo);
                  jc.endUndouableChange(toSelect);
                  graph.refresh();
                  ((Graph) graph).getGraphController().setUpdateInProgress(false);
                  return;
               }
            }
            setChanges();
         } finally {
            e.consume();
         }
      }

      /**
       * Sets the changes to XPDL model.
       */
      protected void setChanges() {
         JaWEController jc = JaWEManager.getInstance().getJaWEController();
         boolean isucinprogress = jc.isUndoableChangeInProgress();
         if (!isucinprogress) {
            ((Graph) graph).getGraphController().setUpdateInProgress(true);
            jc.startUndouableChange();
         }
         if (edgeModified) {
            int noOfPoints = edge.getPointCount();
            List pnts = new ArrayList();
            for (int i = 1; i < noOfPoints - 1; i++) {
               pnts.add(new Point((int) edge.getPoint(i).getX(), (int) edge.getPoint(i)
                  .getY()));// HM, JGraph3.4.1
            }
            GraphUtilities.setBreakpoints((XMLCollectionElement) ((GraphTransitionInterface) edge.getCell()).getPropertyObject(),
                                          pnts);
         }
         ConnectionSet cs = createConnectionSet(edge, false);
         Map nested = GraphConstants.createAttributes(new CellView[] {
            edge
         }, null);
         graph.getGraphLayoutCache().edit(nested, cs, null, null);
         if (!isucinprogress) {
            GraphTransitionInterface tr = (GraphTransitionInterface) edge.getCell();
            List toSelect = new ArrayList();
            toSelect.add(tr.getPropertyObject());
            jc.endUndouableChange(toSelect);
            ((Graph) graph).getGraphController().setUpdateInProgress(false);
         }
      }

      /**
       * Cleans the rendered transition.
       * 
       * @param tr Transition to clean.
       */
      protected void clean(GraphTransitionInterface tr) {
         Graphics g = graph.getGraphics();
         overlay(g);
         firstOverlayCall = true;
         graph.removeSelectionCell(tr);
         return;
      }

   }

   /**
    * Creates new renderer for the given {@link Transition} or {@link Association} object.
    * 
    * @param tra
    * @return The renderer.
    */
   protected GraphTransitionRendererInterface createRenderer(XMLCollectionElement tra) {
      return GraphUtilities.getGraphController()
         .getGraphObjectRendererFactory()
         .createTransitionRenderer(tra);
   }
}
