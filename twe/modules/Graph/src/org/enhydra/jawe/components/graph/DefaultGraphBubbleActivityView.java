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

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;

/**
 * Represents a view for a model's End object.
 * @author Sasa Bojanic
 */
public class DefaultGraphBubbleActivityView extends DefaultGraphActivityView {

   /**
   * Constructs a End view for the specified model object.
   *
   * @param cell reference to the model object
   */
   public DefaultGraphBubbleActivityView(Object cell) {
      super(cell);
   }

   public CellViewRenderer getRenderer() {
      String type=((GraphActivityInterface)super.getCell()).getType();
      GraphActivityRendererInterface garenderer=(GraphActivityRendererInterface)renderers.get(type);
      if (garenderer==null) {
         garenderer=createRenderer(type);
         renderers.put(type,garenderer);
      }
      return garenderer;
   }

   protected GraphActivityRendererInterface createRenderer (String type) {
      return GraphUtilities.getGraphController().getGraphObjectRendererFactory().createBubbleRenderer(type);
   }
   
   public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
      return ((DefaultGraphBubbleActivityRenderer)getRenderer()).getPerimeterPoint(this, p);
   }
}
