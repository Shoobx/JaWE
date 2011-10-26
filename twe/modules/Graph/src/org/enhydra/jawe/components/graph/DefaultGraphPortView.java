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
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;

/**
 * Represents a view for a graph Port object.
 * 
 * @author Sasa Bojanic
 */
public class DefaultGraphPortView extends GraphPortViewInterface {

   /** Map of renderers */
   protected static Map renderers = new HashMap();

   /**
    * Creates new object.
    * 
    * @param cell {@link GraphPortInterface} instance.
    */
   public DefaultGraphPortView(Object cell) {
      super(cell);
      AttributeMap map = new AttributeMap();
      GraphConstants.setSize(map, new Dimension(30, 30));
      super.setAttributes(map);
   }

   public void setPortSize(Dimension d) {
      if (SIZE < 2)
         SIZE = 2;
      AttributeMap map = new AttributeMap();
      GraphConstants.setSize(map, d);
      super.setAttributes(map);
   }

   public Dimension getPortsSize() {
      return (Dimension) getAttributes().get(GraphConstants.SIZE);
   }

   public GraphCommonInterface getGraphActivityOrArtifact() {
      return (GraphCommonInterface) getParentView().getCell();
   }

   public CellViewRenderer getRenderer() {
      String type = ((GraphPortInterface) super.getCell()).getType();
      GraphPortRendererInterface gprenderer = (GraphPortRendererInterface) renderers.get(type);
      if (gprenderer == null) {
         gprenderer = createRenderer(type);
         renderers.put(type, gprenderer);
      }
      return gprenderer;
   }

   public Rectangle2D getBounds() {
      AttributeMap map = new AttributeMap();
      Rectangle2D bounds = map.createRect(getLocation());
      bounds.setFrame(bounds.getX() - getPortsSize().width / 2,
                      bounds.getY() - getPortsSize().height / 2,
                      bounds.getWidth() + getPortsSize().width,
                      bounds.getHeight() + getPortsSize().height);
      return bounds;
   }

   /**
    * Creates new renderer for the given port type.
    * 
    * @param type Port type.
    * @return Renderer for this port.
    */
   protected GraphPortRendererInterface createRenderer(String type) {
      return GraphUtilities.getGraphController()
         .getGraphObjectRendererFactory()
         .createPortRenderer(type);
   }

}
