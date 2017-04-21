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

import org.jgraph.graph.PortView;

/**
 * Abstract class for representing a view for a JGraph's port objects.
 * 
 * @author Sasa Bojanic
 */
public abstract class GraphPortViewInterface extends PortView {

   /**
    * Creates new object.
    * 
    * @param cell {@link GraphPortInterface} instance.
    */
   public GraphPortViewInterface(Object cell) {
      super(cell);
   }

   /**
    * Sets size of all ports to given value.
    * 
    * @param d The dimension of port object.
    */
   public abstract void setPortSize(Dimension d);

   /**
    * @return The dimension of port object.
    */
   public abstract Dimension getPortsSize();

   /**
    * @return {@link GraphActivityInterface} or {@link GraphArtifactInterface} for this
    *         port.
    */
   public abstract GraphCommonInterface getGraphActivityOrArtifact();
}
