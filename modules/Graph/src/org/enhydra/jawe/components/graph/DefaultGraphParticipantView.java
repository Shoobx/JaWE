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

import org.jgraph.JGraph;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.VertexView;

/**
 * Represents a view for a model's Participant object.
 *
 * @author Sasa Bojanic
 */
public class DefaultGraphParticipantView extends VertexView implements GraphParticipantViewInterface {

   protected static Map renderers = new HashMap();

   /**
   * Constructs a participant view for the specified model object.
   *
   * @param cell reference to the model object
   */
   public DefaultGraphParticipantView(Object cell) {
      super(cell);
   }

   /**
   * Returns a renderer for the class.
   */
   public CellViewRenderer getRenderer() {
      String type=((GraphParticipantInterface)super.getCell()).getType();
      GraphParticipantRendererInterface gprenderer=(GraphParticipantRendererInterface)renderers.get(type);
      if (gprenderer==null) {
         gprenderer=createRenderer(((GraphParticipantInterface)super.getCell()).getUserObject());
         renderers.put(type,gprenderer);
      }
      return gprenderer;
   }

   /**
   * Returns true if name portion of ParticipantView intersects the given rectangle.
   */
   public boolean intersects(JGraph graph, Rectangle2D rect) {//HM, JGraph3.4.1
      Rectangle2D lBounds = getBounds();//HM, JGraph3.4.1
      boolean rotateParticipant = GraphUtilities.getGraphOrientation(((Graph) graph).getXPDLObject()).equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ORIENTATION_VALUE_VERTICAL);
      
      if (lBounds != null) {
      	 Rectangle nameBounds;
      	 if (rotateParticipant)
      	 	nameBounds=new Rectangle((int)lBounds.getX(),(int)lBounds.getY(),
      	 			(int)lBounds.getWidth(), GraphUtilities.getGraphController().getGraphSettings().getLaneNameWidth());
      	 else
      	 	nameBounds=new Rectangle((int)lBounds.getX(),(int)lBounds.getY(),
                 GraphUtilities.getGraphController().getGraphSettings().getLaneNameWidth(), (int)lBounds.getHeight());
         return nameBounds.intersects(rect);
      }
      return false;
   }

	/**
	 * Clears childView's and then refreshes it.
	 */
	public void refreshChildViews(GraphLayoutCache cache, CellMapper mapper) {
		childViews.clear();
		refresh(cache, mapper,  false);
	}

   /**
   * Returns the bounding rectangle for this view.
   */
   public Rectangle2D getBounds() {//HM, JGraph3.4.1
      return bounds;
   }

   /**
   * Adds participants child view to it.
   */
   public void addChildView (CellView childView) {
      if (childView != null) {
         childViews.add(childView);
      }
   }

   protected GraphParticipantRendererInterface createRenderer (Object par) {
      return GraphUtilities.getGraphController().getGraphObjectRendererFactory().createParticipantRenderer(par);
   }   

}
