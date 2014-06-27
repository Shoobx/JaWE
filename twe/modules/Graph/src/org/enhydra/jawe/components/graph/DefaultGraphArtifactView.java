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
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Artifact;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

/**
 * Represents a view for a model's Artifact object.
 */
public class DefaultGraphArtifactView extends VertexView implements GraphArtifactViewInterface {

   /** Map of renderers for different types of artifacts. */
   protected static Map renderers = new HashMap();

   /**
    * The list of strings representing "caller method" names. When the caller of
    * getBounds() method is some of the specified methods, the original "basic" bounds are
    * returned, the "full" bounds are returned otherwise.
    */
   protected static List mthlst = Arrays.asList(new String[] {
         "intersection", "getPerimeterPoint", "getCenterPoint", "paintPort", "getCBounds"
   });

   /**
    * Constructs a artifact view for the specified model object.
    * 
    * @param cell reference to the model object
    */
   public DefaultGraphArtifactView(Object cell) {
      super(cell);
   }

   public CellViewRenderer getRenderer() {
      String type = ((GraphArtifactInterface) super.getCell()).getType();
      GraphArtifactRendererInterface garenderer = (GraphArtifactRendererInterface) renderers.get(type);
      if (garenderer == null) {
         garenderer = createRenderer((Artifact) ((GraphArtifactInterface) super.getCell()).getUserObject());
         renderers.put(type, garenderer);
      }
      return garenderer;
   }

   public Rectangle2D getBounds() {// HM, JGraph3.4.1
      GraphArtifactInterface gact = (GraphArtifactInterface) getCell();
      Artifact art = (Artifact) gact.getUserObject();
      String mn = Utils.getCallerMethodName(0);
      if (art.getArtifactType().equals(XPDLConstants.ARTIFACT_TYPE_ANNOTATION) || mthlst.contains(mn)) {
         // System.out.println("MN=" + mn + " - b=" + bounds);
         return bounds;
      }

      int lp = GraphUtilities.getLabelLocation(art);
      Dimension d = ((GraphArtifactRendererInterface) getRenderer()).getLabelDimension(this);
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
      // System.out.println("MN=" + mn + " - r=" + rect);
      return rect;
   }

   public Rectangle getOriginalBounds() {
      return bounds.getBounds();
   }

   public void setOriginalBounds(Rectangle bounds) {
      this.bounds = bounds;
   }

   /**
    * Creates a renderer object for a given XPDL artifact object.
    * 
    * @param art The artifact from XPDL model.
    * @return Renderer object.
    */
   protected GraphArtifactRendererInterface createRenderer(Artifact art) {
      return GraphUtilities.getGraphController().getGraphObjectRendererFactory().createArtifactRenderer(art);
   }

   public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
      return ((MultiLinedRenderer) getRenderer()).getPerimeterPoint(this, p);
   }

}
