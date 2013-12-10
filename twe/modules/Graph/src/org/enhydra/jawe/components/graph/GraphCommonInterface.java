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

import java.awt.Point;
import java.util.Set;

import org.jgraph.graph.DefaultGraphCell;

/**
 * Interface for defining Activity graph object.
 *
 * @author Sasa Bojanic
 */
public abstract class GraphCommonInterface extends DefaultGraphCell implements WorkflowElement, Linkable {

   /**
    * Gets the port associated with this object.
    */
   public abstract GraphPortInterface getPort ();

   /**
    * Gets all activities that reference this one.
    */
   public abstract Set getReferencingActivities ();

   /**
    * Gets all activities that this activity references.
    */
   public abstract Set getReferencedActivities ();

   public abstract Point getOffset ();
   
   
}
