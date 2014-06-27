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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.enhydra.jawe.Utils;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.elements.Activity;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

/**
 * Represents a view for a model's Activity object.
 */
public class DefaultGraphActivityView extends VertexView implements GraphActivityViewInterface {

   /** A map of renderers for different activity types. */
   protected static Map renderers = new HashMap();

   /**
    * The list of strings representing "caller method" names. When the caller of
    * getBounds() method is some of the specified methods, the original "basic" bounds are
    * returned, the "full" bounds are returned otherwise.
    */
   protected static List mthlst = Arrays.asList(new String[] {
         "intersection", "getPerimeterPoint", "getCenterPoint", "getLocation", "paintPort", "getCBounds"
   });

   /**
    * Constructs a activity view for the specified model object.
    * 
    * @param cell reference to the model object
    */
   public DefaultGraphActivityView(Object cell) {
      super(cell);
   }

   public CellViewRenderer getRenderer() {
      String type = ((GraphActivityInterface) super.getCell()).getType();
      GraphActivityRendererInterface garenderer = (GraphActivityRendererInterface) renderers.get(type);
      if (garenderer == null) {
         garenderer = createRenderer((Activity) ((GraphActivityInterface) super.getCell()).getUserObject());
         renderers.put(type, garenderer);
      }
      return garenderer;
   }

   public Rectangle2D getBounds() {// HM, JGraph3.4.1
      String mn = Utils.getCallerMethodName(0);
      // System.out.println("MN="+mn);
      if (mthlst.contains(mn)) {
         return bounds;
      }
      GraphActivityInterface a = (GraphActivityInterface) cell;
      Activity act = ((Activity) a.getUserObject());
      int lp = GraphUtilities.getLabelLocation(act);
      Dimension d = ((GraphActivityRendererInterface) getRenderer()).getLabelDimension(this);
      int dx = 0;
      int dy = (int) bounds.getHeight();
      if (lp == GraphEAConstants.LABEL_POSITION_LEFT || lp == GraphEAConstants.LABEL_POSITION_RIGHT) {
         dy = (int) (bounds.getHeight() / 2 - d.getHeight() / 2);
         dx = (int) d.getWidth() + 5;
         if (lp == GraphEAConstants.LABEL_POSITION_RIGHT) {
            dx = -dx - (int) bounds.getWidth();
         }
      } else {
         dx = d.getWidth() > bounds.getWidth() ? (int) (d.getWidth() - bounds.getWidth()) / 2 : 0;
         if (lp == GraphEAConstants.LABEL_POSITION_TOP) {
            dy = -(int)d.getHeight();
         }
      } 
      Rectangle2D rect = new Rectangle((int) (bounds.getX() - dx), (int) (bounds.getY() + dy), (int) d.getWidth(), (int) d.getHeight());
      if (rect != null)
         Rectangle2D.union(bounds, rect, rect);
      return rect;
   }

   public Rectangle getOriginalBounds() {
      // System.out.println("OB "+getCell()+" ="+bounds.getBounds());
      return bounds.getBounds();
   }

   public void setOriginalBounds(Rectangle bounds) {
      this.bounds = bounds;
   }

   /**
    * Creates a renderer object for a given XPDL activity object.
    * 
    * @param act The activity from XPDL model.
    * @return Renderer object.
    */
   protected GraphActivityRendererInterface createRenderer(Activity act) {
      return GraphUtilities.getGraphController().getGraphObjectRendererFactory().createActivityRenderer(act);
   }

   public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
      CellViewRenderer r = getRenderer();
      if (r instanceof DefaultGraphEventActivityRenderer) {
         return ((DefaultGraphEventActivityRenderer) getRenderer()).getPerimeterPoint(this, p);
      }
      return ((MultiLinedRenderer) getRenderer()).getPerimeterPoint(this, p);
   }

   // public CellHandle getHandle(GraphContext context) {
   // if (GraphConstants.isSizeable(getAllAttributes())
   // && !GraphConstants.isAutoSize(getAllAttributes())
   // && context.getGraph().isSizeable())
   // return new MySizeHandle(this, context);
   // return null;
   // }
   //
   // public static class MySizeHandle extends SizeHandle {
   // public MySizeHandle(VertexView vertexview, GraphContext ctx) {
   // super(vertexview,ctx);
   // }
   //
   // public void mouseReleased(MouseEvent e) {
   // if (index != -1) {
   // cachedBounds = computeBounds(e);
   // System.out.println("CB="+cachedBounds);
   // vertex.setBounds(cachedBounds);
   // CellView[] views = AbstractCellView
   // .getDescendantViews(new CellView[] { vertex });
   // Map attributes = GraphConstants.createAttributes(views, null);
   // graph.getGraphLayoutCache().edit(attributes, null, null, null);
   // }
   // e.consume();
   // cachedBounds = null;
   // initialBounds = null;
   // firstDrag = true;
   //
   // }
   // }

}
