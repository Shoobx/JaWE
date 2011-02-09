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

import java.awt.Dimension;

import org.jgraph.graph.PortView;

/**
 * Interface for representing a view for a JGraph's port objects.
 *
 * @author Sasa Bojanic   
 */
public abstract class GraphPortViewInterface extends PortView {
   
   public GraphPortViewInterface(Object cell) {
      super(cell);
   } 
   
   public abstract void setPortSize (Dimension d);

   public abstract Dimension getPortsSize ();
 
   public abstract GraphCommonInterface getGraphActivityOrArtifact ();
}
