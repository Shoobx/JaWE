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

import org.jgraph.graph.DefaultPort;

/**
 * Abstract class for defining graph port object.
 * 
 * @author Sasa Bojanic
 */
public abstract class GraphPortInterface extends DefaultPort {

   /**
    * Creates new port.
    * 
    * @param name The name of the port.
    */
   public GraphPortInterface(String name) {
      super(name);
   }

   /**
    * @return {@link GraphActivityInterface} or {@link GraphArtifactInterface} object for
    *         this port.
    */
   public abstract GraphCommonInterface getActivityOrArtifact();

   /**
    * @return The type of this port.
    */
   public abstract String getType();

}
