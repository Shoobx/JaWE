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

package org.enhydra.jawe.components.graph;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.Pool;
import org.jgraph.JGraph;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.VertexView;

/**
 * Represents a view for a model's swimlane object.
 */
public class DefaultGraphSwimlaneView extends VertexView implements
                                                        GraphSwimlaneViewInterface {

   /** Map of renderers for different types of swimlanes. */
   protected static Map renderers = new HashMap();

   /**
    * Constructs a swimlane view for the specified model object.
    * 
    * @param cell reference to the model object
    */
   public DefaultGraphSwimlaneView(Object cell) {
      super(cell);
   }

   public CellViewRenderer getRenderer() {
      String type = ((GraphSwimlaneInterface) super.getCell()).getType();
      GraphSwimlaneRendererInterface gprenderer = (GraphSwimlaneRendererInterface) renderers.get(type);
      if (gprenderer == null) {
         gprenderer = createRenderer(((GraphSwimlaneInterface) super.getCell()).getUserObject());
         renderers.put(type, gprenderer);
      }
      return gprenderer;
   }

   public boolean intersects(JGraph graph, Rectangle2D rect) {// HM, JGraph3.4.1
      Rectangle2D lBounds = getBounds();// HM, JGraph3.4.1
      boolean rotateParticipant = GraphUtilities.getGraphOrientation(((Graph) graph).getXPDLObject())
         .equals(XPDLConstants.POOL_ORIENTATION_VERTICAL);

      if (lBounds != null) {
         Rectangle nameBounds;
         if (rotateParticipant)
            nameBounds = new Rectangle((int) lBounds.getX(),
                                       (int) lBounds.getY(),
                                       (int) lBounds.getWidth(),
                                       GraphUtilities.getGraphController()
                                          .getGraphSettings()
                                          .getLaneNameWidth());
         else
            nameBounds = new Rectangle((int) lBounds.getX(),
                                       (int) lBounds.getY(),
                                       GraphUtilities.getGraphController()
                                          .getGraphSettings()
                                          .getLaneNameWidth(),
                                       (int) lBounds.getHeight());
         return nameBounds.intersects(rect);
      }
      return false;
   }

   public void refreshChildViews(GraphLayoutCache cache, CellMapper mapper) {
      childViews.clear();
      refresh(cache, mapper, false);
   }

   public Rectangle2D getBounds() {// HM, JGraph3.4.1
      return bounds;
   }

   /**
    * Adds swimlane child view to the list of views.
    * 
    * @param childView
    */
   public void addChildView(CellView childView) {
      if (childView != null) {
         childViews.add(childView);
      }
   }

   /**
    * Creates a renderer object for a given XPDL swimlane object (Pool or Lane).
    * 
    * @param par The {@link Pool} or {@link Lane} from XPDL model.
    * @return Renderer object.
    */
   protected GraphSwimlaneRendererInterface createRenderer(Object par) {
      return GraphUtilities.getGraphController()
         .getGraphObjectRendererFactory()
         .createParticipantRenderer(par);
   }

}
