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

/**
* Defines the requirements for an object that
* represents a cell that should be linked with
* other cells.
*/

public interface Linkable {
   /**
   * Returns <code>true</code> if cell that implements it is a valid source for link.
   */
   boolean acceptsSource();

   /**
   * Returns <code>true</code> if cell that implements it is a valid target for link.
   */
   boolean acceptsTarget();

}

