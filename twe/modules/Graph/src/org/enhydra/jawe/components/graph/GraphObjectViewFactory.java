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
      if (cell instanceof GraphParticipantInterface) {
         return new DefaultGraphParticipantView(cell);
      } else if (cell instanceof GraphBubbleActivityInterface) {
         return new DefaultGraphBubbleActivityView(cell);
      } else if (cell instanceof GraphActivityInterface) {
         return new DefaultGraphActivityView(cell);
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