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

import java.util.List;

import org.jgraph.graph.Edge;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphLayoutCache;

/**
 * Used to define Transition object in the graph.
 */
public class NoRouting implements Edge.Routing {
	
	public List route(GraphLayoutCache arg0, EdgeView arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPreferredLineStyle(EdgeView arg0) {
	// TODO Auto-generated method stub
	return NO_PREFERENCE;
}

}
