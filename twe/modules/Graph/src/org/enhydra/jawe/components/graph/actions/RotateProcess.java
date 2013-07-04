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
 * Miroslav Popov, Aug 4, 2005
 */
package org.enhydra.jawe.components.graph.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.jawe.components.graph.JaWEGraphModel;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.Transition;
import org.jgraph.graph.DefaultGraphCell;

/**
 * @author Miroslav Popov
 */
public class RotateProcess extends ActionBase {

   public RotateProcess(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      GraphController gc = (GraphController) jawecomponent;

      if (gc.getSelectedGraph() != null)
         if (XMLUtil.getPackage(gc.getSelectedGraph().getXPDLObject()) == jc.getMainPackage()) {
            setEnabled(true);
            return;
         }

      setEnabled(false);
   }

   public void actionPerformed(ActionEvent e) {
      GraphController gcon = (GraphController) jawecomponent;
      Graph selectedGraph = gcon.getSelectedGraph();
      if (selectedGraph == null)
         return;

      gcon.setUpdateInProgress(true);
      JaWEManager.getInstance().getJaWEController().startUndouableChange();
      if (GraphUtilities.getGraphOrientation(selectedGraph.getXPDLObject())
         .equals(XPDLConstants.POOL_ORIENTATION_HORIZONTAL)) {
         GraphUtilities.setGraphOrientation(selectedGraph.getXPDLObject(),
                                            XPDLConstants.POOL_ORIENTATION_VERTICAL);
      } else {
         GraphUtilities.setGraphOrientation(selectedGraph.getXPDLObject(),
                                            XPDLConstants.POOL_ORIENTATION_HORIZONTAL);
      }

      GraphUtilities.rotateProcess(selectedGraph);

      List toSelect = new ArrayList();
      toSelect.add(selectedGraph.getXPDLObject());
      JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
      selectedGraph.clearSelection();
      gcon.setUpdateInProgress(false);
   }

}
