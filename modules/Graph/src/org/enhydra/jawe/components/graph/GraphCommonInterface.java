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
 * Abstract class for defining common things for Activity and Artifact graph objects.
 * 
 * @author Sasa Bojanic
 */
public abstract class GraphCommonInterface extends DefaultGraphCell implements
                                                                   WorkflowElement,
                                                                   Linkable {

   /**
    * @return The port associated with this object.
    */
   public abstract GraphPortInterface getPort();

   /**
    * @return A set of {@link GraphActivityInterface} instances which have outgoing
    *         connections to this object.
    */
   public abstract Set getReferencingActivities();

   /**
    * @return A set of {@link GraphActivityInterface} instances which have incoming
    *         connections from this object.
    */
   public abstract Set getReferencedActivities();

   /**
    * @return The offset point of this object within the graph.
    */
   public abstract Point getOffset();

}
