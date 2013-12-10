package org.enhydra.jawe.components.graph;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.undo.UndoableEdit;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.ParentMap;

/**
 * A process editor implementation of a graph model.
 * @author Sasa Bojanic
 */
public class JaWEGraphModel extends DefaultGraphModel implements Serializable {

	public JaWEGraphModel() {
		super();
		setRemoveEmptyGroups(false);
	}
	
	public JaWEGraphModel(List roots, AttributeMap attributes) {
		super(roots, attributes);
		setRemoveEmptyGroups(false);
	}
	
	public JaWEGraphModel(List roots, AttributeMap attributes,
			ConnectionSet cs) {
		super(roots, attributes, cs);
		setRemoveEmptyGroups(false);
	}	
	
	/**
	 * Inserts the <code>roots</code> and connections into the model.
	 * Notifies the model- and undo listeners of the change. The passed-in
	 * edits are executed if they implement the
	 * <code>GraphModelEvent.ExecutableGraphChange</code> interface
	 * in ascending array-order, after execution of the model change.
	 * (Note: The external order is important in a
	 * special case: After insertion on a partial view, ie. one that does not
	 * display all cells of the model, the cell is made visible after
	 * it is inserted into the model. This requires the inserting view
	 * to be able to add the cell to the visible set before it is
	 * inserted into the model.)
	 * Note: The passed-in propertyMap may contains PortViews
	 * which must be turned into Points when stored in the model.
	 */
	public void insertAndEdit(Object[] pRoots, Map pAttributes, ConnectionSet pCs, ParentMap pPm, UndoableEdit[] pEdits) {
		GraphModelEdit edit = createInsertEdit(pRoots, pAttributes, pCs, pPm, pEdits);
		if (edit != null) {
			edit.execute();
			if (pEdits != null) {
				for (int i = 0; i < pEdits.length; i++)
					if (pEdits[i] instanceof GraphLayoutCache.GraphLayoutCacheEdit)
						((GraphLayoutCache.GraphLayoutCacheEdit) pEdits[i]).execute();
			}
			postEdit(edit);
		}
	}
	
	/**
	 * Removes <code>cells</code> from the model and edits cells given in
	 * a <code>propertyMap</code>. If <code>removeChildren</code>
	 * is <code>true</code>, the children are also removed.
	 * Notifies the model- and undo listeners of the change.
	 */
	public void removeAndEdit(Object[] pRoots, Map pAttributes) {
		GraphModelEdit edit = createRemoveAndCellEdit(pRoots, pAttributes);
		if (edit != null) {
			edit.execute();
			postEdit(edit);
		}
	}
	
	/**
	 * Used for editing font sizes without haveing an undo action set.
	 */
	public void editFonts(Map attributes) {
		GraphModelEdit edit = createCellEdit(attributes, null, null, null);
		//System.out.println("DefaultGraphModel_edit_attributes="+attributes);
		if (edit != null) {
			edit.execute();
		}
	}


	/**
	 * Returns an edit that represents an insert.
	 */
	protected GraphModelEdit createInsertEdit(Object[] cells, Map attributeMap, ConnectionSet cs, ParentMap pm,
			UndoableEdit[] edits) {
		//GraphModelEdit edit = new GraphModelEdit(cells, cs, pm, attributeMap);
		GraphModelEdit edit = createEdit(cells, null, attributeMap, cs, pm);
		if (edit != null) {
			if (edits != null) for (int i = 0; i < edits.length; i++)
				edit.addEdit(edits[i]);
			edit.end();
		}
		return edit;
	}

	/**
	 * Returns an edit that represents a remove and a change.
	 */
	protected GraphModelEdit createRemoveAndCellEdit(Object[] cells, Map attributes) {
		// Remove from GraphStructure
		ConnectionSet cs = ConnectionSet.create(this, cells, true);
		// Remove from Group Structure
		ParentMap pm = ParentMap.create(this, cells, true, false);
		// Construct Edit
		GraphModelEdit edit = createEdit(null, cells, attributes, cs, pm);
		if (edit != null) {
			edit.end();
		}
		return edit;
	}
	
	/**
	 * Returns an edit that represents a change.
	 */
	protected GraphModelEdit createCellEdit(Map attributes, ConnectionSet cs, ParentMap pm, UndoableEdit[] edits) {
		//GraphModelEdit edit = new GraphModelEdit(cs, propertyMap, pm);
		GraphModelEdit edit = createEdit(null, null, attributes, cs, pm);
		if (edit != null) {
			if (edits != null) for (int i = 0; i < edits.length; i++)
				edit.addEdit(edits[i]);
			edit.end();
		}
		return edit;
	}
	
	protected GraphModelEdit createEdit(Object[] inserted, Object[] removed, Map attributes, ConnectionSet cs,
			ParentMap pm) {
		return new GraphModelEdit(inserted, removed, attributes, cs, pm);
	}
	
	/**
	 * Gets all existing cells within model.
	 */
	public static List getAllCellsInModel(GraphModel model) {
		List allCellsInModel = getDescendants(model, getRoots(model));
		if (allCellsInModel == null || allCellsInModel.size() == 0) {
			return null;
		}

		return allCellsInModel;
	}

	/**
	 * Returns the root participants of the specified model as a set.
	 */
	public static Set getRootParticipants(GraphModel model) {
		Object[] roots = getRoots(model);
		if (roots == null || roots.length == 0) return null;

		Set rootDeps = new HashSet();

		// extracting only participants (transitions are also roots)
		for (int i = 0; i < roots.length; i++) {
			if (roots[i] instanceof GraphParticipantInterface) {
				rootDeps.add(roots[i]);
			}
		}

		return rootDeps;
	}

	
	/**
	 * Gets all existing participants within model.
	 */
	public static List getAllParticipantsInModel(GraphModel model) {
		if (!(model instanceof JaWEGraphModel)) return null;

		List allCellsInModel = getAllCellsInModel(model);
		if (allCellsInModel == null) {
			return null;
		}

		List participants = new LinkedList();
		Iterator it = allCellsInModel.iterator();
		while (it.hasNext()) {
			Object cell = it.next();
			if (cell instanceof GraphParticipantInterface) {
				participants.add(cell);
			}
		}
		if (participants.size() == 0) {
			return null;
		}

		return participants;
	}

	/**
	 * Gets all existing activities within model.
	 */
	public static List getAllActivitiesInModel(GraphModel model) {
		if (!(model instanceof JaWEGraphModel)) return null;

		List allCellsInModel = getAllCellsInModel(model);
		if (allCellsInModel == null) {
			return null;
		}

		List activities = new LinkedList();
		Iterator it = allCellsInModel.iterator();
		while (it.hasNext()) {
			Object cell = it.next();
			if (cell instanceof GraphActivityInterface) {
				activities.add(cell);
			}
		}
		if (activities.size() == 0) {
			return null;
		}

		return activities;
	}

   /**
    * Gets all existing start/end bubbles within model. 
    */
   public static List getAllBubblesInModel(GraphModel model) {
      if (!(model instanceof JaWEGraphModel)) return null;

      List allCellsInModel = getAllCellsInModel(model);
      if (allCellsInModel == null) {
         return null;
      }

      List bubbles = new LinkedList();
      Iterator it = allCellsInModel.iterator();
      while (it.hasNext()) {
         Object cell = it.next();
         if (cell instanceof GraphBubbleActivityInterface) {
            bubbles.add(cell);
         }
      }
      if (bubbles.size() == 0) {
         return null;
      }

      return bubbles;
   }   
   
	/**
	 * Gets all existing transitions within model.
	 */
	public static List getAllTransitionsInModel(GraphModel model) {
		if (!(model instanceof JaWEGraphModel)) return null;
		// Transitions are root objects
		Object[] roots = getRoots(model);
		if (roots == null || roots.length == 0) return null;

		List transitions = new LinkedList();

		// extracting only transitions (participants are also roots)
		for (int i = 0; i < roots.length; i++) {
			if (roots[i] instanceof GraphTransitionInterface) {
				transitions.add(roots[i]);
			}
		}

		return transitions;
	}
	
}