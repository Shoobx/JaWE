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

/* PEGraphSelectionModel.java
 *
 * Authors:
 * Stefanovic Nenad  chupo@iis.ns.ac.yu
 * Bojanic Sasa      sasaboy@neobee.net
 * Puskas Vladimir   vpuskas@eunet.yu
 * Pilipovic Goran   zboniek@uns.ac.yu
 *
 */

package org.enhydra.jawe.components.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphSelectionModel;

/**
* JaWE implementation of selection model.
*/
public class JaWEGraphSelectionModel extends DefaultGraphSelectionModel {

	/** Constructs a DefaultGraphSelectionModel for the specified graph. */
	public JaWEGraphSelectionModel(JGraph graph) {
		super(graph);
	}

	/**
	* Modifies original method to avoid selection of in/out of subflow.
	*/
	public void setSelectionCells(Object[] cells) {
		super.setSelectionCells(removeForbiddenCells(cells));
      graph.getModel().toFront(cells);
	}

	/**
	* Modifies original method to avoid selection of in/out of subflow.
	*/
	public void addSelectionCells(Object[] cells) {
		super.addSelectionCells(removeForbiddenCells(cells));
      graph.getModel().toFront(cells);
	}

	/**
	* Modifies original method to avoid deselection of in/out of subflow.
	*/
	public void removeSelectionCells(Object[] cells) {
      Vector change = new Vector();
      HashSet parents = new HashSet(); 
      for (int i = 0; i < cells.length; i++) {
           if (((DefaultGraphCell)cells[i]).getParent() != null) {
              parents.add(((DefaultGraphCell)cells[i]).getParent());
              deselect(cells[i]);
              change.addElement(new JaWECellPlaceHolder(cells[i], false));
           } else {
              deselect(cells[i]);
              change.addElement(new JaWECellPlaceHolder(cells[i], false));
           }
      }
      
      for (Iterator it = parents.iterator(); it.hasNext();) {
         DefaultGraphCell parent = (DefaultGraphCell) it.next();
         List children = parent.getChildren();
         boolean hasSelected = false;
         for (int i = 0; i < children.size(); i++) {
            if (graph.isCellSelected(children.get(i))) {
               hasSelected = true;
               break;
            }
         }
         if (!hasSelected) {
            deselect(parent);
            change.addElement(new JaWECellPlaceHolder(parent, false));
         }
      }
   
      if (change.size() > 0)
         notifyCellChange(change);
	}

	/**
	* Removes forbiden objects from selection.
	*/
	private Object[] removeForbiddenCells (Object[] cells) {
		/*
		if (cells != null && cells.length>0) {
			Set cellsToSelect=new HashSet();
			for (int i=0; i<cells.length; i++) {
				if (!(cells[i] instanceof SubflowPort)) {
					cellsToSelect.add(cells[i]);
				}
			}
			if (cellsToSelect.size()>0) {
				cells=cellsToSelect.toArray();
			}
			else {
				cells=null;
			}
		}
		*/
		return cells;
	}

	// NOTE: FOLLOWING METHOD IS NEVER CALLED BECAUSE PEJGRAPH IMPLEMENTS
	//       IT'S OWN METHOD TO DO THIS. IT COULD BE CHANGED.
	/**
	* Returns the cells that are currently selectable.
	*/
    public Object[] getSelectables() {
      Object[] selectables=super.getSelectables();

      if (selectables!=null && selectables.length>0) {
      	Set removedForbiddenElements=new HashSet();
      	for (int i=0; i<selectables.length; i++) {
      		//if (!(selectables[i] instanceof SubflowPort)) {
      			removedForbiddenElements.add(selectables[i]);
      		//}
      	}
      	selectables=removedForbiddenElements.toArray();
      }
     	return selectables;
   }

    
   protected class JaWECellPlaceHolder extends DefaultGraphSelectionModel.CellPlaceHolder {
      
      protected JaWECellPlaceHolder(Object cell, boolean isNew) {
         super(cell, isNew);
      }
   }
}

/* End of PEGraphSelectionModel.java */
