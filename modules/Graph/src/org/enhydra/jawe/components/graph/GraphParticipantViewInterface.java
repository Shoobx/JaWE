package org.enhydra.jawe.components.graph;

import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphLayoutCache;

/**
 * Interface for representing a view for a JGraph's Participant objects.
 *
 * @author Sasa Bojanic
 */
public interface GraphParticipantViewInterface extends CellView {
	
	void refreshChildViews(GraphLayoutCache cache, CellMapper mapper);
	
}
