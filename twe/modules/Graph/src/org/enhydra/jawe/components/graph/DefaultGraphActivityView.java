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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.enhydra.shark.xpdl.elements.Activity;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

/**
 * Represents a view for a model's Activity object.
 *
 * @author Sasa Bojanic
 */
public class DefaultGraphActivityView extends VertexView implements GraphActivityViewInterface {

   protected static Map renderers = new HashMap();

   /**
   * Constructs a activity view for the specified model object.
   *
   * @param cell reference to the model object
   */
   public DefaultGraphActivityView(Object cell) {
      super(cell);
   }

   /**
   * Returns a renderer for the class.
   */
   public CellViewRenderer getRenderer() {      
      String type=((GraphActivityInterface)super.getCell()).getType();
      GraphActivityRendererInterface garenderer=(GraphActivityRendererInterface)renderers.get(type);
      if (garenderer==null) {
         garenderer=createRenderer((Activity)((GraphActivityInterface)super.getCell()).getUserObject());
         renderers.put(type,garenderer);
      }
      return garenderer;
   }

   /**
   * Returns the bounding rectangle for this view.
   */
   public Rectangle2D getBounds() {//HM, JGraph3.4.1
      return bounds;
   }

   protected GraphActivityRendererInterface createRenderer (Activity act) {
      return GraphUtilities.getGraphController().getGraphObjectRendererFactory().createActivityRenderer(act);
   }
 
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		return ((MultiLinedRenderer)getRenderer()).getPerimeterPoint(this, p);
	}
}
