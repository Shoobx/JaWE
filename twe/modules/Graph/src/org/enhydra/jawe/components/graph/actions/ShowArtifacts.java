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

package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Shows artifacts and associations in the graph.
 * 
 * @author Sasa Bojanic
 */
public class ShowArtifacts extends ActionBase {

   public ShowArtifacts(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController) jawecomponent;
      setEnabled(gc.getSelectedGraph() != null
                 && !gc.getSelectedGraph().getXPDLObject().isReadOnly() && !gc.getSelectedGraph().shouldShowArtifacts());
   }

   public void actionPerformed(ActionEvent e) {
      GraphController gcon = (GraphController) jawecomponent;

      gcon.getSelectedGraph().setShowArtifacts(true);
      gcon.getSelectedGraph().getGraphManager().showArtifacts(true);
      
      gcon.getSelectedGraph().refresh();
      gcon.adjustActions();
   }
}
