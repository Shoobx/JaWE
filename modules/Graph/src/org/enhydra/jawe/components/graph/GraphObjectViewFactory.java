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

/**
 * JaWE CellViewFactory implementation
 *
 * Miroslav Popov, Jul 7, 2005 
 */
package org.enhydra.jawe.components.graph;

import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.PortView;

/** 
 * @author Miroslav Popov
 */
public class GraphObjectViewFactory implements CellViewFactory {
	/* (non-Javadoc)
	 * @see org.jgraph.graph.CellViewFactory#createView(org.jgraph.graph.GraphModel, java.lang.Object)
	 */
	public CellView createView(GraphModel model, Object cell) {
		CellView view = null;
		if (model.isPort(cell))
			view = createPortView(cell);
		else if (model.isEdge(cell))
			view = createEdgeView(cell);
		else
			view = createVertexView(cell);

		return view;
	}

	/**
	 * Create different vertex view depending of cell type
	 * 
	 * @param cell
	 * @return CellView which represent view of cell
	 */
	protected CellView createVertexView(Object cell) {
      if (cell instanceof GraphSwimlaneInterface) {
         return new DefaultGraphSwimlaneView(cell);
      } else if (cell instanceof GraphActivityInterface) {
         return new DefaultGraphActivityView(cell);
      } else if (cell instanceof GraphArtifactInterface){
         return new DefaultGraphArtifactView(cell);
      } else {
          return null;
      }
	}
	
	/**
	 * Return edge view. For now we have only transition as edges
	 * 
	 * @param cell
	 * @return EdgeView
	 */	
   protected EdgeView createEdgeView(Object cell) {
      if (cell instanceof GraphTransitionInterface) 
         return new DefaultGraphTransitionView(cell);

     return null;
   }
   
   /**
    * We don't want many ports to be shown. Instead we want only one as big as vertex view is
    * 
    * @param cell
    * @return JaWEPortView - redefined port view
    */
   protected PortView createPortView(Object cell) {
      if (cell instanceof GraphPortInterface) 
         return new DefaultGraphPortView(cell);
         
         return null;
   }
}