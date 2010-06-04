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
